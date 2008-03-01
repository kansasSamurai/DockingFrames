/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.gui.dock.common.intern.theme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.gui.dock.dockable.DockableMovingImageFactory;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.themes.ColorProviderFactory;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.util.Priority;
import bibliothek.gui.dock.util.color.ColorManager;
import bibliothek.gui.dock.util.color.ColorProvider;
import bibliothek.gui.dock.util.color.DockColor;

/**
 * A {@link DockTheme} that wraps another theme and works within
 * the special environment the common-project provides.
 * @author Benjamin Sigg
 */
public class CDockTheme<D extends DockTheme> implements DockTheme {
    /** the original theme */
    private D delegate;
    
    /** the factories used in this theme */
    private Map<Class<?>, ColorProviderFactory<?, ?>> colorProviderFactories =
        new HashMap<Class<?>, ColorProviderFactory<?,?>>();
    
    /** the settings of all {@link DockController}s */
    private List<Controller> controllers = new ArrayList<Controller>();
    
    /**
     * Creates a new theme 
     * @param delegate a normal {@link DockTheme}
     */
    public CDockTheme( D delegate ){
        if( delegate == null )
            throw new IllegalArgumentException( "delegate must not be null" );
        
        this.delegate = delegate;
    }
    
    /**
     * Gets the internal representation of this theme.
     * @return the internal representation
     */
    public D intern(){
        return delegate;
    }
    
    public Combiner getCombiner( DockStation station ) {
        return delegate.getCombiner( station );
    }

    public DisplayerFactory getDisplayFactory( DockStation station ) {
        return delegate.getDisplayFactory( station );
    }

    public DockableMovingImageFactory getMovingImageFactory( DockController controller ) {
        return delegate.getMovingImageFactory( controller );
    }

    public StationPaint getPaint( DockStation station ) {
        return delegate.getPaint( station );
    }

    public DockTitleFactory getTitleFactory( DockController controller ) {
        return delegate.getTitleFactory( controller );
    }

    /**
     * Sets the {@link ColorProvider} which should be used for a certain kind
     * of {@link DockColor}s. The providers will be installed with priority
     * {@link Priority#DEFAULT} at all {@link ColorManager}s.
     * @param <C> the kind of {@link DockColor} the providers will handle
     * @param kind the kind of {@link DockColor} the providers will handle
     * @param factory the factory for new providers
     */
    @SuppressWarnings("unchecked")
    public <C extends DockColor> void putColorProviderFactory( Class<C> kind, ColorProviderFactory<C, ? extends ColorProvider<C>> factory ){
        colorProviderFactories.put( kind, factory );
        for( Controller setting : controllers ){
            ColorManager colors = setting.controller.getColors();
            
            ColorProvider<?> oldProvider = setting.providers.remove( kind );
            ColorProvider<?> newProvider = factory == null ? null : factory.create( colors );
            
            if( newProvider == null ){
                setting.providers.remove( kind );
                
                if( oldProvider != null )
                    colors.unpublish( Priority.DEFAULT, kind );
            }
            else{
                setting.providers.put( kind, newProvider );
                colors.publish( Priority.DEFAULT, (Class<DockColor>)kind, (ColorProvider<DockColor>)newProvider );
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void install( DockController controller ) {
        delegate.install( controller );
        
        Controller settings = new Controller();
        settings.controller = controller;
        
        ColorManager colors = controller.getColors();
        for( Map.Entry<Class<?>, ColorProviderFactory<?, ?>> entry : colorProviderFactories.entrySet() ){
            ColorProvider<DockColor> provider = (ColorProvider<DockColor>)entry.getValue().create( colors );
            colors.publish( 
                    Priority.DEFAULT, 
                    (Class<DockColor>)entry.getKey(), 
                    provider );
            settings.providers.put( entry.getKey(), provider );
        }
        controllers.add( settings );
    }

    public void uninstall( DockController controller ) {
        delegate.uninstall( controller );
        
        for( int i = 0, n = controllers.size(); i<n; i++ ){
            Controller settings = controllers.get( i );
            if( settings.controller == controller ){
                controllers.remove( i );
                
                ColorManager colors = controller.getColors();
                for( ColorProvider<?> provider : settings.providers.values() ){
                    colors.unpublish( Priority.DEFAULT, provider );
                }
            }
        }
    }
    
    /**
     * A structure containing settings for a specific {@link DockController}.
     * @author Benjamin Sigg
     */
    private class Controller{
        /** the controller which is represented by this element */
        public DockController controller;
        /** the list of {@link ColorProvider}s which is used in that controller */
        public Map<Class<?>,ColorProvider<?>> providers = new HashMap<Class<?>, ColorProvider<?>>();
    }
}