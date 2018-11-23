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
	
	Method getVal = null;
	Method setVal = null;
	Attribute val;
	ConflictResolutionFunction<ValueType, RecordType, SchemaElementType> fusingMechanism;

    public GenericFuser(Attribute val, Method method, Method method2, ConflictResolutionFunction<ValueType, RecordType, SchemaElementType> fusingMechanism) {
        super(fusingMechanism);
       	this.val = val;
        this.getVal = method;
        this.setVal = method2;
        this.fusingMechanism = fusingMechanism;
    }

    public String getValStrategy() {
    	return this.val.toString() + " -> " + fusingMechanism.getClass().getName();
    }



	@Override
	public ValueType getValue(RecordType record, Correspondence<SchemaElementType, Matchable> correspondence) {
		try {
			return (ValueType) record.getClass().getMethod(getVal.getName(), null).invoke(record, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void fuse(RecordGroup<RecordType, SchemaElementType> group, RecordType fusedRecord,
			Processable<Correspondence<SchemaElementType, Matchable>> schemaCorrespondences,
			SchemaElementType schemaElement) {
		FusedValue<ValueType, RecordType, SchemaElementType> fused = getFusedValue(group, schemaCorrespondences, schemaElement);
       
		try {
			fusedRecord.getClass().getMethod(setVal.getName(), setVal.getParameterTypes()[0]).invoke(fusedRecord, fused.getValue());
		} catch (Exception e) {
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean hasValue(RecordType record, Correspondence<SchemaElementType, Matchable> correspondence) {
		return record.hasValue((SchemaElementType) val);
	}



}
