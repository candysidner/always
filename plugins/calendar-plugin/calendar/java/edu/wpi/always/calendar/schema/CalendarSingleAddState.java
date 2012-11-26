package edu.wpi.always.calendar.schema;

import java.util.*;

import org.joda.time.*;

import edu.wpi.always.calendar.schema.CalendarAdjacencyPairs.Cancel;
import edu.wpi.always.calendar.schema.CalendarAdjacencyPairs.EventDayAdjacencyPair;
import edu.wpi.always.calendar.schema.CalendarAdjacencyPairs.EventPersonAdjacencyPair;
import edu.wpi.always.calendar.schema.CalendarAdjacencyPairs.EventTypeAdjacencyPair;
import edu.wpi.always.calendar.schema.CalendarAdjacencyPairs.HowLongAdjacencyPair;
import edu.wpi.always.calendar.schema.CalendarAdjacencyPairs.TimeAdjacencyPair;
import edu.wpi.always.calendar.schema.CalendarAdjacencyPairs.WhereAdjacencyPair;
import edu.wpi.always.cm.dialog.*;
import edu.wpi.always.user.calendar.*;
import edu.wpi.always.user.people.*;

abstract class CalendarSingleAddState {

	public static class EventType extends EventTypeAdjacencyPair {
		private CalendarEntry newEntry;
		public EventType(CalendarStateContext context) {
			this(context, new CalendarEntryImpl(null, null, null, null, null, null));
		}
		public EventType(CalendarStateContext context, CalendarEntry newEntry) {
			super(context);
			this.newEntry = newEntry;
			if(newEntry.getType()!=null)
				skipTo(new EventDay(newEntry, getContext()));
		}

		@Override
		public AdjacencyPair nextState(CalendarEntryType type) {
			newEntry.setType(type);
			type.prefill(newEntry);
			return new EventPerson(newEntry, getContext());
		}

	}

	public static class EventPerson extends EventPersonAdjacencyPair {

		private final CalendarEntry data;
		public EventPerson(final CalendarEntry data, final CalendarStateContext context) {
			super(data.getType().getPersonQuestion()!=null?data.getType().getPersonQuestion():"", context);
			this.data = data;
			if(data.getType().getPersonQuestion()==null || data.getPeople().size()>0)
				skipTo(new EventDay(data, getContext()));
		}

		@Override
		public AdjacencyPair nextState(Person person) {
			data.addPerson(person);
			return new EventDay(data, getContext());
		}

	}

	public static class EventDay extends EventDayAdjacencyPair {

		private final CalendarEntry data;
		public EventDay(final CalendarEntry data, final CalendarStateContext context) {
			super("Is the "+data.getDisplayTitle()+" during this week", context, data.getStart()==null?new LocalDate():CalendarUtil.getDate(data.getStart()));
			this.data = data;
			if(data.getStart()!=null)
				skipTo(new HowLong(data, getContext()));
		}

		@Override
		public AdjacencyPair nextState(LocalDate date) {
			data.setStart(CalendarUtil.toDateTime(date, new LocalTime()));
			return new WhenStart(data, new LocalTime(10, 0), getContext());
		}

	}

	private static class WhenStart extends TimeAdjacencyPair {
		private final CalendarEntry entry;

		public WhenStart(final CalendarEntry entry, final LocalTime startTime, final CalendarStateContext context) {
			super("What time does the "+entry.getDisplayTitle()+" start", startTime, context);
			this.entry = entry;
			choice("Start Over", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new Cancel(context);
				}
			});
		}

		@Override
		public TimeAdjacencyPair changeStartTime(LocalTime time) {
			return new WhenStart(entry, time, getContext());
		}

		@Override
		public AdjacencyPair nextState(LocalTime time) {
			entry.setStart(CalendarUtil.toDateTime(CalendarUtil.getDate(entry.getStart()), time));
			return new HowLong(entry, getContext());
		}
	}

	private static class HowLong extends HowLongAdjacencyPair {
		private final CalendarEntry data;

		public HowLong(CalendarEntry data, final CalendarStateContext context) {
			super(context);
			this.data = data;
			if(data.getDuration()!=null)
				skipTo(new Where(data, getContext()));
		}

		@Override
		public AdjacencyPair nextState(ReadablePeriod d) {
			data.setDuration(d);
			return new Where(data, getContext());
		}
	}

	private static class Where extends WhereAdjacencyPair {
		private final CalendarEntry entry;

		public Where(CalendarEntry entry, CalendarStateContext context) {
			super(context);
			this.entry = entry;
			if(entry.getStart()!=null)
				skipTo(new Ok(entry, getContext()));
		}

		@Override
		public AdjacencyPair nextState(String place) {
			entry.setPlace(getContext().getPlaceManager().getPlace(place));
			return new Ok(entry, getContext());
		}
	};

	private static class Ok extends CalendarAdjacencyPairImpl{
		private final CalendarEntry data;
		public Ok(CalendarEntry data, final CalendarStateContext context) {
			super("Okay, give me a moment to set this up", context);
			this.data = data;
		}
		public List<String> getChoices() {
			return null;
		}
		@Override
		public AdjacencyPair nextState(String text) {
			return new Thanks(data, getContext());
		}
	};


	private static class Thanks extends CalendarAdjacencyPairImpl{
		private final CalendarEntry data;
		public Thanks(final CalendarEntry data, final CalendarStateContext context) {
			super("Okay, here is the entry", context);
			this.data = data;
			choice("Thanks", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new WhatDo(context);
				}
			});
			choice("Oh that's not quite right", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new OkLetsChangeItThen(data, context);
				}
			});
		}
		@Override
		public void enter(){
			getContext().getCalendar().create(data);
			getContext().getCalendarUI().showWeek(CalendarUtil.getDate(data.getStart()), null, false);
		}
	};
	private static class OkLetsChangeItThen extends CalendarAdjacencyPairImpl{
		public OkLetsChangeItThen(final CalendarEntry data, final CalendarStateContext context) {
			super("Lets start over and just change the event", context);
			choice("Ok", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new CalendarChangeState.WhatChange(data, context, false);
				}
			});
		}
	};
}