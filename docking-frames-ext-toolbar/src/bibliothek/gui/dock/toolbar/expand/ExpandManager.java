package bibliothek.gui.dock.toolbar.expand;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.Rectangle;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ExpandableToolbarItemStrategy;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.screen.ScreenDockWindow;
import bibliothek.gui.dock.station.screen.magnet.MagnetizedOperation;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * The {@link ExpandManager} is responsible for performing global effects that happen
 * after a {@link ExpandableToolbarItemStrategy} changed the {@link ExpandedState} of 
 * a {@link Dockable}. 
 * @author Benjamin Sigg
 */
public class ExpandManager {
	/** the currently active strategy */
	private PropertyValue<ExpandableToolbarItemStrategy> strategy = new PropertyValue<ExpandableToolbarItemStrategy>( ExpandableToolbarItemStrategy.STRATEGY ){
		@Override
		protected void valueChanged( ExpandableToolbarItemStrategy oldValue, ExpandableToolbarItemStrategy newValue ){
			if( oldValue != null ){
				oldValue.removeExpandedListener( listener );
			}
			if( newValue != null ){
				newValue.addExpandedListener( listener );
			}
		}
	};
	
	/** this listener is added to the currently active {@link #strategy} */
	private ExpandableToolbarItemStrategyListener listener = new ExpandableToolbarItemStrategyListener(){
		@Override
		public void stretched( Dockable item ){
			updateLater( item );
		}
		
		@Override
		public void shrunk( Dockable item ){
			updateLater( item );
		}
		
		@Override
		public void expanded( Dockable item ){
			updateLater( item );
		}
		
		@Override
		public void enablementChanged( Dockable item, ExpandedState state, boolean enabled ){
			// ignore
		}
	};
	
	public ExpandManager( DockController controller ){
		strategy.setProperties( controller );
	}
	
	private void updateLater( final Dockable item ){
		EventQueue.invokeLater( new Runnable(){
			@Override
			public void run(){
				update( item );
			}
		});
	}
	
	/**
	 * Called after the {@link ExpandedState} of <code>item</code> changed. This method will check the position and
	 * size of <code>item</code> and if possible change the size such that it matches the preferred size
	 * of the <code>item</code>.
	 * @param item the item whose state changed
	 */
	public void update( Dockable item ){
		DockStation parent = item.getDockParent();
		while( parent != null ){
			if( parent instanceof ScreenDockStation ){
				update( (ScreenDockStation)parent, item );
			}
			
			item = parent.asDockable();
			if( item == null ){
				parent = null;
			}
			else{
				parent = item.getDockParent();
			}
		}
	}
	
	/**
	 * Called if the {@link ExpandedState} of <code>child</code> or one of its children has changed. This method
	 * should update the size of <code>child</code> such that it has its preferred size
	 * @param station the parent of <code>child</code>
	 * @param child the child whose state changed
	 */
	protected void update( ScreenDockStation station, Dockable child ){
		ScreenDockWindow window = station.getWindow( child );
		Insets insets = window.getDockableInsets();
		
		Dimension preferred = child.getComponent().getPreferredSize();
		
		int width = insets.left + insets.right + preferred.width;
		int height = insets.top + insets.bottom + preferred.height;
		
		Rectangle bounds = window.getNormalBounds();
		if( bounds == null ){
			bounds = window.getWindowBounds();
		}
		bounds = new Rectangle( bounds.x, bounds.y, width, height );
		
		MagnetizedOperation operation = station.getMagnetController().start( window );
		
		Rectangle validated = station.getBoundaryRestriction().check( window, bounds );
		if( validated != null ){
			bounds = validated;
		}
		bounds = operation.attract( bounds );
		
		operation.stop();
		
		if( window.isFullscreen() ){
			window.setNormalBounds( bounds );
		}
		else{
			window.setWindowBounds( bounds, true );
		}
	}
}
