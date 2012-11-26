package edu.wpi.always.cm.realizer;

import java.util.*;
import java.util.concurrent.*;

public abstract class CompoundRealizerImplBase implements CompoundRealizer {

	protected final List<CompoundRealizerObserver> observers = new CopyOnWriteArrayList<CompoundRealizerObserver>();
	
	protected void notifyDone() {
		for(CompoundRealizerObserver o : observers) {
			o.compoundRealizerDone(this);
		}
	}

	@Override
	public void addObserver(CompoundRealizerObserver observer) {
		observers.add(observer);
	}

	@Override
	public void removeObserver(CompoundRealizerObserver observer) {
		observers.remove(observer);
	}

}