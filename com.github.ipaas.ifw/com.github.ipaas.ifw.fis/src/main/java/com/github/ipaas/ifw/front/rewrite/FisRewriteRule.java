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

package com.github.ipaas.ifw.front.rewrite;

import java.util.List;

/**
 *
 * @author Chenql
 */
public class FisRewriteRule {
	private String requestUri;
	private String templateFile;
	private List<String> dataFiles;
	private String contentType;

	public FisRewriteRule(String requestUri, String templateFile, List<String> dataFiles, String contentType) {
		this.requestUri = requestUri;
		this.templateFile = templateFile;
		this.dataFiles = dataFiles;
		this.contentType = contentType;
	}

	public String getRequestUri() {
		return requestUri;
	}

	public String getTemplateFile() {
		return templateFile;
	}

	public List<String> getDataFiles() {
		return dataFiles;
	}

	public String getContentType() {
		return contentType;
	}

}
