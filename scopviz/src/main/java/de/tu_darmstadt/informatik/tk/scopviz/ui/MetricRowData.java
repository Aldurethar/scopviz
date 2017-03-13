package de.tu_darmstadt.informatik.tk.scopviz.ui;

import de.tu_darmstadt.informatik.tk.scopviz.metrics.ScopvizGraphMetric;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class MetricRowData {
	
	//TODO umbennen, wenn verwendet wird 
	 private SimpleStringProperty key;
	 private SimpleStringProperty value;
	 private SimpleBooleanProperty checked;
	 ScopvizGraphMetric metric;
	 
	   public MetricRowData(ScopvizGraphMetric metric) {
		  this.metric = metric;
	      this.key = new SimpleStringProperty(this.metric.getName());
	      this.value = new SimpleStringProperty(this.metric.calculate(null).getFirst().getValue());
	      this.checked = new SimpleBooleanProperty(false);
	   }
	 
	   public String getKey() {
	      return key.get();
	   }
	 
	   public String getValue() {
	      return value.get();
	   }
	   
	   public boolean getChecked() {
		   
	      return checked.get();
	   }
	 	   
	   public SimpleBooleanProperty checkedProperty() {
	      return checked;
	   }
	   
}
