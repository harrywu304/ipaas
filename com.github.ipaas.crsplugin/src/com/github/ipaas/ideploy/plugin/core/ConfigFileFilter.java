 
package com.github.ipaas.ideploy.plugin.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tools.ant.util.StringUtils;

import com.github.ipaas.ideploy.plugin.bean.FilterPattern;
import com.github.ipaas.ideploy.plugin.util.ConsoleHandler;
import com.github.ipaas.ideploy.plugin.util.JsonUtil;

/**
 * 配置文加过滤器
 * 
 * @author Chenql  
 */
public class ConfigFileFilter {

	List<FilterPattern> patternList = new ArrayList<FilterPattern>();
	Set<String> prefixPatternSet = new HashSet<String>();
	Set<String> suffixPatternSet = new HashSet<String>();

	public ConfigFileFilter(List<String> patternJsonList) {
		if (patternJsonList != null && patternJsonList.size() > 0) {
			for (String json : patternJsonList) {
				FilterPattern pattern = JsonUtil.toBean(json, FilterPattern.class);
				patternList.add(pattern);
				if (pattern.isChecked() && pattern.getPattern() != null) {
					prefixPatternSet.add(pattern.getPattern());
				}
				// else if (pattern.isChecked() && pattern.getPattern() !=
				// null && pattern.getPattern().startsWith("*")) {
				// suffixPatternSet.add(StringUtils.removePrefix(pattern.getPattern(),
				// "*"));
				// // System.out.println("--------------->  " +
				// // pattern.getPattern());
				// }
			}
		}
	}

	public List<String> filterResult(List<String> compareResult) {
		List<String> result = new ArrayList<String>();
		for (String str : compareResult) {
			String path = str.startsWith("+") ? StringUtils.removePrefix(str, "+/") : StringUtils.removePrefix(str,
					"-/");
			boolean filtered = false;
			for (String prefix : prefixPatternSet) {
				if (path.startsWith(prefix)) {
					ConsoleHandler.info(path + " 文件已被过滤");
					filtered = true;
					break;
				}
			}
			if (filtered) {
				continue;
			}
			// 后缀过滤暂时不提供
			// for (String suffix : suffixPatternSet) {
			// if (path.endsWith(suffix)) {
			// System.out.println(path + " 文件已被过滤");
			// filtered = true;
			// break;
			// }
			// }
			// if (filtered) {
			// continue;
			// }
			result.add(str);
		}
		return result;
	}

}
