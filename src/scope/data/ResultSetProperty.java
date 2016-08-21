package scope.data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.ResultSet;

import javax.swing.event.SwingPropertyChangeSupport;

public class ResultSetProperty {

	private ResultSet resultSet = null;

	  private SwingPropertyChangeSupport changes = new SwingPropertyChangeSupport( this );

	  public void setResultSet(ResultSet resultSet )
	  {
	    this.resultSet = resultSet;
	    changes.firePropertyChange( "ResultSet", null, resultSet);
	  }

	  public ResultSet getResultSet()
	  {
	    return resultSet;
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
