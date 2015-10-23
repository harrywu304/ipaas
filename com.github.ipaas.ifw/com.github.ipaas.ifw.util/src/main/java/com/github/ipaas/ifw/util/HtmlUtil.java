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
package com.github.ipaas.ifw.util;

import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;

/**
 * html工具
 * 
 * @author Chenql
 *
 */
public class HtmlUtil {
	/**
	 * 把字符串中含有的Html特殊字符转化为常规字符，如果Http请求的参数值是字符串， 则需调用该方法进行字符转码处理。
	 * 
	 * @param str
	 *            要转换的字符串
	 * @return 转换后的字符串
	 */
	public static String encodeHtml(String str) {
		if (str == null || str.equals("")) {
			return "";
		}
		// 把要处理的字符串转换为字符数组
		char content[] = new char[str.length()];
		str.getChars(0, str.length(), content, 0);
		// 循环处理字符数组
		StringBuffer result = new StringBuffer(content.length + 50);
		char preChar = ' ';
		for (int i = 0; i < content.length; i++) {
			switch (content[i]) {
			case '<':
				result.append("&lt;");
				break;
			case '>':
				result.append("&gt;");
				break;
			case '&':
				result.append("&amp;");
				break;
			case '"':
				result.append("&quot;");
				break;
			case ' ':
				result.append("&nbsp;");
				break;
			case '\r':
				result.append("<br>");
				break;
			case '\n':
				// 检查前一个字符，是否'\r'，对'\r\n'我们只转换成一个<br>
				if (preChar != '\r') {
					result.append("<br>");
				}
				break;
			default:
				result.append(content[i]);
			}
			preChar = content[i];
		}
		return (result.toString());
	}

	/**
	 * 把字符串中含有的Html特殊字符转化为常规字符，如果Http请求的参数值是字符串， 则需调用该方法进行字符转码处理。
	 * 注意：该方法与encodeHtml的不同在于默认会对字符串按照论坛发帖的需求进行格式化，所以该方法主要用于类似论坛发帖的需求
	 * 
	 * @param str
	 *            要转换的字符串
	 * @return 转换后的字符串
	 */
	@Deprecated
	public static String encodeHtmlTag(String str) {
		if (str == null || str.equals("")) {
			return "";
		}
		str = formatContent(str);
		return encodeHtml(str);
	}

	/**
	 * 过滤font标签
	 * 
	 * @param str
	 * @return
	 */
	public static String filterFontTag(String str) {
		if (str == null || str.length() == 0) {
			return str;
		}
		return str.replaceAll("<font.*?</font>", "");
	}

	/**
	 * 过滤href标签
	 * 
	 * @param str
	 * @return
	 */
	public static String filterHrefTag(String str) {
		if (str == null || str.length() == 0) {
			return str;
		}
		return str.replaceAll("<a.*?</a>", "");
	}

	/**
	 * 过滤html标签
	 * 
	 * @param str
	 * @return　返回把html过滤后的内容
	 * @throws Exception
	 */
	public static String filterHtmlTag(String str) {
		return filterHtmlTag(str, "utf-8");
	}

	/**
	 * 过滤html标签
	 * 
	 * @param str
	 *            　
	 * @param character
	 *            字符集
	 * @return
	 * @throws Exception
	 */
	public static String filterHtmlTag(String str, String character) {
		if (str == null || str.equals("")) {
			return "";
		}
		str = "<html>" + str + "</html>";
		StringBuffer strBff = new StringBuffer();
		try {
			Parser parser = Parser.createParser(new String(str.getBytes(character), character), character);
			NodeList nodes = parser.extractAllNodesThatMatch(new NodeFilter() {
				public boolean accept(Node node) {
					return true;
				}
			});
			Node node = nodes.elementAt(0);
			strBff.append(new String(node.toPlainTextString().getBytes(character), character));
		} catch (Exception e) {

		}
		return strBff.toString();
	}

	/**
	 * 过滤javaScript标签
	 * 
	 * @param str
	 * @return
	 */
	public static String filterJavaScriptTag(String str) {
		if (str == null || str.length() == 0
				|| (str.toLowerCase().indexOf("script") < 0 && str.toLowerCase().indexOf("javascript") < 0)) {
			return str;
		}
		return Pattern
				.compile("\\<(\\s*)(script)(\\s.*?)?\\>((\\s|.)*?)\\<\\/(\\s*)(script)(\\s.*?)?\\>",
						Pattern.CASE_INSENSITIVE).matcher(str).replaceAll("");
	}

	/**
	 * 过滤换行符标签,在内容修改和回复修改时用到
	 * 
	 * @param str
	 * @return
	 */
	public static String filterRowTag(String str) {
		if (str == null || str.length() == 0) {
			return str;
		}
		return str.replaceAll("\r\n", "<br>").replaceAll("\n", "<br>").replaceAll("\r", "<br>");
	}

	/**
	 * 格式帖子内容信息
	 * 
	 * @param str
	 * @return
	 */
	public static String formatContent(String str) {
		// 得到数组
		String[] contents = str.split("\r");
		if (contents == null) {
			return "　　" + str.trim().replace("　　", "");
		}
		StringBuffer content = new StringBuffer("");
		for (int i = 0; i < contents.length; i++) {
			if (i == 0) {
				// 内容最前只插入两个空格
				content.append("　　" + contents[i].trim().replace("　　", ""));
			} else {
				content.append("\r　　" + contents[i].trim().replace("　　", ""));
			}
		}
		return content.toString();
	}

	/**
	 * 判断是否bmp文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean isBmp(String fileName) {
		if (fileName.toLowerCase().endsWith(".bmp")) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否gif文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean isGif(String fileName) {
		if (fileName.toLowerCase().endsWith(".gif")) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否jpg文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean isJpg(String fileName) {
		if (fileName.toLowerCase().endsWith(".jpg")) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否png文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean isPng(String fileName) {
		if (fileName.toLowerCase().endsWith(".png")) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否Jpeg文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean isJpeg(String fileName) {
		if (fileName.toLowerCase().endsWith(".jpeg")) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否有效的图片url.
	 * 
	 * @param url
	 * @return
	 */
	public static boolean isPicUrl(String url) {
		if (StringUtil.isNullOrBlank(url)) {
			return false;
		}
		// 当后缀为.png,.bmp,.gif,.jpg是返回true。
		return isPng(url) || isBmp(url) || isGif(url) || isJpg(url) || isJpeg(url);
	}

	/**
	 * 反向转换html字符串，一般用在textarea中正确显示编辑内容
	 * 
	 * @param str
	 * @return 转换后的字符串
	 */
	public static String decodeHtml(String str) {
		if (str == null || str.equals("")) {
			return str;
		}

		// str = str.replaceAll("''", "'");
		str = str.replaceAll("<br>", "\r");
		str = str.replaceAll("&nbsp;", " ");
		str = str.replaceAll("&quot;", "\"");
		str = str.replaceAll("&lt;", "<");
		str = str.replaceAll("&gt;", ">");
		str = str.replaceAll("&amp;", "&");
		return str;

	}

	/**
	 * 反向转换html字符串，一般用在textarea中正确显示编辑内容。
	 * 
	 * @param str
	 * @return 转换后的字符串
	 * @deprecated 用decodeHtml代替，两方法实现一样，命名不一样
	 */
	@Deprecated
	public static String unEncodeHtmlTag(String str) {
		return decodeHtml(str);
	}

}
