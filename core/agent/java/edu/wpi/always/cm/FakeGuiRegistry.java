package edu.wpi.always.cm;

import org.picocontainer.*;

import edu.wpi.always.PicoRegistry;
import edu.wpi.always.cm.perceptors.gui.*;
import edu.wpi.always.cm.schemas.*;


public class FakeGuiRegistry implements PicoRegistry, SchemaRegistry {

	private GuiFakePerceptors gui = new GuiFakePerceptors();

	@Override
	public void register(SchemaManager manager) {
		SchemaFactory factory = new SchemaFactory() {
			
			@Override
			public Class<? extends Schema> getSchemaType() {
				return AquariumTripSchema.class;
			}
			
			@Override
			public Schema create(PicoContainer container) {
				return new AquariumTripSchema(container.getComponent(BehaviorProposalReceiver.class),
						container.getComponent(BehaviorHistory.class), gui.getTxtAquariumTrip());
			}

			@Override
			public long getUpdateDelay() {
				return Schema.DEFAULT_INTERVAL;
			}
		};
		
		manager.registerSchema(factory, true);
	}

	@Override
	public void register(MutablePicoContainer container) {
		container.addComponent(gui.createMovementPerceptor());
		container.addComponent(gui.createFacePerceptor());
		
		java.awt.EventQueue.invokeLater(new Runnable() {

			public void run () {
				try {
					gui.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}