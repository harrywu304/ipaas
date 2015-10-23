/**
* Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *    *      http://www.apache.org/licenses/LICENSE-2.0  *    * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License..
*/

package com.github.ipaas.ideploy.agent.util;

/** 
 * SVN日志
 * @author  wudg
 */

public class SVNLog{
	 
	public final static String TYPE_ADD="+";
	
	public final static String TYPE_DEL="-";
	
	public final static int ENTRY_FILE=1;
	
	public final static int ENTRY_DIR=2;
	 
	private Long revision;
	
	private String path;
	
	private String type;
	
	private int entryKind;
	
	public SVNLog(){}
	
	public SVNLog(Long revision,String path,String type,int entryKind){
		this.revision=revision;
		this.path=path;
		this.type=type;
		this.entryKind=entryKind; 
	}
	 
	public Long getRevision() {
		return revision;
	}
	public void setRevision(Long revision) {
		this.revision = revision;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	 
	public int getEntryKind() {
		return entryKind;
	}

	public void setEntryKind(int entryKind) {
		this.entryKind = entryKind;
	}
	  
	 

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SVNLog other = (SVNLog) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}
	
	public String toSimpleString(){
		return this.getType()+"  "+this.getPath();
	}
	
	
}
