package de.uni_mannheim.informatik.dws.wdi.Restaurants.fusers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import de.uni_mannheim.informatik.dws.winter.datafusion.AttributeFuser;
import de.uni_mannheim.informatik.dws.winter.datafusion.AttributeValueFuser;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.ConflictResolutionFunction;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.Voting;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.FusedValue;
import de.uni_mannheim.informatik.dws.winter.model.Fusible;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroup;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;

public class GenericFuser<ValueType, RecordType extends Matchable & Fusible<SchemaElementType>, SchemaElementType extends Matchable> extends
        AttributeValueFuser<ValueType, RecordType, SchemaElementType> {
	
	Supplier<ValueType> getVal = null;
	Consumer<ValueType> setVal = null;
	Attribute val;

    public GenericFuser(Attribute val, Supplier<ValueType> getVal, Consumer<ValueType> setVal, ConflictResolutionFunction<ValueType, RecordType, SchemaElementType> fusingMechanism) {
        super(fusingMechanism);
       	this.val = val;
        this.getVal = getVal;
        this.setVal = setVal;
    }




	@Override
	public ValueType getValue(RecordType record, Correspondence<SchemaElementType, Matchable> correspondence) {
		for (Method m : record.getClass().getMethods()) {
			if (m.getName().equals(getVal.toString())) {
				 try {
					return (ValueType) m.invoke(record, null);
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} 
		return null;
	}

	@Override
	public void fuse(RecordGroup<RecordType, SchemaElementType> group, RecordType fusedRecord,
			Processable<Correspondence<SchemaElementType, Matchable>> schemaCorrespondences,
			SchemaElementType schemaElement) {
		FusedValue<ValueType, RecordType, SchemaElementType> fused = getFusedValue(group, schemaCorrespondences, schemaElement);
       
        for (Method m : fusedRecord.getClass().getMethods()) {
			if (m.getName().equals(getVal.toString())) {
				 try {
					m.invoke(fusedRecord, fused.getValue());
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} 
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean hasValue(RecordType record, Correspondence<SchemaElementType, Matchable> correspondence) {
		return record.hasValue((SchemaElementType) val);
	}



}
