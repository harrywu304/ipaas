/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ipaas.ideploy.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 发送信息格式
 * @author dengrunquan
 *
 */
public class SendMessageDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5151148490390871567L;

	/**
	 * 查询资源的名称  和表名的对应关系是 ideploy_ori + resourceName
	 */
	private String resourceName;
	
	/**
	 * 数据版本
	 */
	private String dataVersion;
	/**
	 * 日期列
	 */
	private String[] dateColumns;
	
	/**
	 * 和字名一一对应
	 */
	private Map<String,Object> properties;
	
	/**
	 * 初始化
	 */
	public SendMessageDTO(){
		
	}
	
	public SendMessageDTO(String resourceName,Map properties){
		this.resourceName = resourceName;
		this.properties = properties;
	}
	

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public Map<String, Object> getProperties() {
		if(dateColumns!=null && dateColumns.length>0){
			for(String dateColumn : dateColumns){
				Object obj = properties.get(dateColumn);
				if(obj instanceof Long){
					properties.put(dateColumn, new Date((Long)obj));
				}
			}
		}
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	public String[] getDateColumns() {
		return dateColumns;
	}

	public void setDateColumns(String[] dateColumns) {
		this.dateColumns = dateColumns;
	}

	public String getDataVersion() {
		return dataVersion;
	}

	public void setDataVersion(String dataVersion) {
		this.dataVersion = dataVersion;
	}

	public void setDateColumns(List<String> list) {
		if(list!=null){
			this.dateColumns = new String[list.size()];
			for(int i=0;i<list.size();i++){
				dateColumns[i] = list.get(i);
			}
		}
	}

	
	
	
	

}
