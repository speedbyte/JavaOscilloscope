package scope.data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

public class ArrayListProperty {

	private ArrayList<double[]> list = new ArrayList<double[]>();

	  private PropertyChangeSupport changes = new PropertyChangeSupport( this );

	  public void setList(ArrayList<double[]> list )
	  {
	    this.list = list;
	    changes.firePropertyChange("Datalist", null, list);
	  }

	  public ArrayList<double[]> getList()
	  {
	    return list;
	  }

	  public void addPropertyChangeListener( PropertyChangeListener l )
	  {
	    changes.addPropertyChangeListener( l );
	  }

	  public void removePropertyChangeListener( PropertyChangeListener l )
	  {
	    changes.removePropertyChangeListener( l );
	  }
	
}
