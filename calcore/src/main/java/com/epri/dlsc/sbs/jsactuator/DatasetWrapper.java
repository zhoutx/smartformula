package com.epri.dlsc.sbs.jsactuator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.epri.dlsc.sbs.exception.DataNotUnique;
import com.epri.dlsc.sbs.dataset.DataSetExpression;
import com.epri.dlsc.sbs.dataset.DataSetField;
import com.epri.dlsc.sbs.dataset.ResultSet.Row;
import com.epri.dlsc.sbs.inter.datasource.BaseDataSource;
import com.epri.dlsc.sbs.inter.loader.Loader;
import com.epri.dlsc.sbs.cal.log.Logger;

/**
 * 数据集包装器
 * @author Zhong.Weijian
 *
 */
public class DatasetWrapper {
	private static Logger logger = Logger.getLogger(DatasetWrapper.class);

	private Loader calculateDriver;
	
	public DatasetWrapper(Loader calculateDriver){
		this.calculateDriver = calculateDriver;
	}
	
	public double eval(DataSetExpression datasetExps, Row calRow, FormulaItemContext context){
		
		String dataSetID = datasetExps.getDatasetId();
		
		BaseDataSource cds = (BaseDataSource) calculateDriver.getDataSetDefine().getDataSource(dataSetID);
		
		String uniqueConstraintId = cds.getUniqueConstraintId(calRow, datasetExps);

		Row data = null;
		try {
			data = cds.getUniqueData(uniqueConstraintId);
		} catch (DataNotUnique dataNotUnique) {
			dataNotUnique.printStackTrace();
		}

		if(data != null){
			//将获取的数据源数据加入下上文中
			Collection<DataSetField> fields = cds.getDataSetFields();
			Map<String, String> properties = new HashMap<>();
			for(DataSetField field : fields){
				String fieldID = field.getID();
				String fieldValue = data.getFieldValue(fieldID);
				properties.put(fieldID, fieldValue);
			}
			context.set(dataSetID, properties);

			String value = data.getFieldValue(datasetExps.getFiledId());
			logger.debug("数据集数据抓取:" + value);
			if(value != null){
				return Double.valueOf(value);
			}
			return 0;
		}else{
			return 0;
		}
	}
}
