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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.ipaas.ifw.util.HtmlUtil;

/**
 * @author whx
 * 
 */
public class HtmlUtilTest {

	/**
	 * Test method for {@link com.github.ipaas.ifw.util.HtmlUtil#encodeHtml(java.lang.String)}.
	 */
	@Test
	public void testEncodeHtml() {
		String htmlInit = "看电影\r\n请点击：\n<a href='http://foo.com/?name=harry&city=gz'>\"harry potter's story\"</a>";
		String htmlEncoded = "看电影<br>请点击：<br>&lt;a&nbsp;href='http://foo.com/?name=harry&amp;city=gz'&gt;&quot;harry&nbsp;potter's&nbsp;story&quot;&lt;/a&gt;";
		assertEquals(htmlEncoded, HtmlUtil.encodeHtml(htmlInit));
		assertEquals("", HtmlUtil.encodeHtml(""));
		assertEquals("", HtmlUtil.encodeHtml(null));
	}

	/**
	 * Test method for {@link com.github.ipaas.ifw.util.HtmlUtil#decodeHtml(java.lang.String)}.
	 */
	@Test
	public void testDecodeHtml() {
		String htmlInit = "看电影\r\r请点击：<a href='http://foo.com/?name=harry&city=gz'>\"harry potter's story\"</a>";
		String htmlDecoded = "看电影<br><br>请点击：&lt;a&nbsp;href='http://foo.com/?name=harry&amp;city=gz'&gt;&quot;harry&nbsp;potter's&nbsp;story&quot;&lt;/a&gt;";
		assertEquals(htmlInit, HtmlUtil.decodeHtml(htmlDecoded));
		assertEquals("", HtmlUtil.decodeHtml(""));
		assertNull(HtmlUtil.decodeHtml(null));
	}

	@Test
	public void testEncodeHtmlTag() {
		String htmlInit = "看电影\r\n请点击：\n<a href='http://foo.com/?name=harry&city=gz'>\"harry potter's story\"</a>";
		String htmlEncoded = "　　看电影<br>　　请点击：<br>&lt;a&nbsp;href='http://foo.com/?name=harry&amp;city=gz'&gt;&quot;harry&nbsp;potter's&nbsp;story&quot;&lt;/a&gt;";
		assertEquals(htmlEncoded, HtmlUtil.encodeHtmlTag(htmlInit));
		assertEquals("", HtmlUtil.encodeHtmlTag(""));
		assertEquals("", HtmlUtil.encodeHtmlTag(null));
	}

	@Test
	public void testUnEncodeHtmlTag() {
		String htmlInit = "看电影\r\r请点击：<a href='http://foo.com/?name=harry&city=gz'>\"harry potter's story\"</a>";
		String htmlDecoded = "看电影<br><br>请点击：&lt;a&nbsp;href='http://foo.com/?name=harry&amp;city=gz'&gt;&quot;harry&nbsp;potter's&nbsp;story&quot;&lt;/a&gt;";
		assertEquals(htmlInit, HtmlUtil.unEncodeHtmlTag(htmlDecoded));
	}

	@Test
	public void testFilterFontTag() {
		String htmlInit = "看电影\r\n<font>请点击：</font>\n<a href='http://foo.com/?name=harry&city=gz'>\"harry potter's story\"</a>";
		String htmlFiltered = "看电影\r\n\n<a href='http://foo.com/?name=harry&city=gz'>\"harry potter's story\"</a>";
		assertEquals(htmlFiltered, HtmlUtil.filterFontTag(htmlInit));
		assertEquals("", HtmlUtil.filterFontTag(""));
		assertNull(HtmlUtil.filterFontTag(null));
	}

	@Test
	public void testFilterHrefTag() {
		String htmlInit = "看电影\r\n<font>请点击：</font>\n<a href='http://foo.com/?name=harry&city=gz'>\"harry potter's story\"</a>";
		String htmlFiltered = "看电影\r\n<font>请点击：</font>\n";
		assertEquals(htmlFiltered, HtmlUtil.filterHrefTag(htmlInit));
		assertEquals("", HtmlUtil.filterHrefTag(""));
		assertNull(HtmlUtil.filterHrefTag(null));
	}

	@Test
	public void testFilterHtmlTag() {
		String htmlInit = "看电影\r\n<font>请点击：</font>\n<a href='http://foo.com/?name=harry&city=gz'>\"harry potter's story\"</a>";
		String htmlFiltered = "看电影\r\n请点击：\n\"harry potter's story\"";
		assertEquals(htmlFiltered, HtmlUtil.filterHtmlTag(htmlInit));
		assertEquals("", HtmlUtil.filterHtmlTag(null));
		assertEquals("", HtmlUtil.filterHtmlTag(""));
	}

	@Test
	public void testFilterJavaScriptTag() {
		String jsInit = "看电影\r\n<font>请点击：</font>\n<a href='http://foo.com/?name=harry&city=gz'>\"harry potter's story\"</a><script> for(i=0;i<1;i++){  document.write('<tr><td class=\"td1\">'+asia_totalmedal[i][1]+'</td><td class=\"td1\">'+asia_totalmedal[i][2]+'</td><td class=\"td1\">'+asia_totalmedal[i][3]+'</td></tr>');  }  </script>";
		String jsFiltered = "看电影\r\n<font>请点击：</font>\n<a href='http://foo.com/?name=harry&city=gz'>\"harry potter's story\"</a>";
		assertEquals(jsFiltered, HtmlUtil.filterJavaScriptTag(jsInit));
		assertEquals("", HtmlUtil.filterJavaScriptTag(""));
		assertEquals(jsFiltered, HtmlUtil.filterJavaScriptTag(jsFiltered));
		assertNull(HtmlUtil.filterJavaScriptTag(null));
	}

	@Test
	public void testFilterRowTag() {
		String htmlInit = "看电影\r\n<font>\r请点击：</font>\n<a href='http://foo.com/?name=harry&city=gz'>\"harry potter's story\"</a>";
		String htmlFiltered = "看电影<br><font><br>请点击：</font><br><a href='http://foo.com/?name=harry&city=gz'>\"harry potter's story\"</a>";
		assertEquals(htmlFiltered, HtmlUtil.filterRowTag(htmlInit));
		assertEquals("", HtmlUtil.filterRowTag(""));
		assertNull(HtmlUtil.filterRowTag(null));
	}

	@Test
	public void testFormatContent() {
		String htmlInit = "看电影\r\n<font>\r请点击：</font>\n<a href='http://foo.com/?name=harry&city=gz'>\"harry potter's story\"</a>";
		String htmlFiltered = "　　看电影\r　　<font>\r　　请点击：</font>\n<a href='http://foo.com/?name=harry&city=gz'>\"harry potter's story\"</a>";
		assertEquals(htmlFiltered, HtmlUtil.formatContent(htmlInit));
		assertEquals("　　", HtmlUtil.formatContent(""));
	}

	@Test
	public void testIsBmp() {
		assertTrue(HtmlUtil.isBmp("sample.bmp"));
		assertTrue(HtmlUtil.isBmp("sample.BMP"));
		assertTrue(HtmlUtil.isBmp("sample.Bmp"));
		assertFalse(HtmlUtil.isBmp("sample.txt"));
	}

	@Test
	public void testIsGif() {
		assertTrue(HtmlUtil.isGif("sample.gif"));
		assertTrue(HtmlUtil.isGif("sample.GIF"));
		assertTrue(HtmlUtil.isGif("sample.Gif"));
		assertFalse(HtmlUtil.isGif("sample.txt"));
	}

	@Test
	public void testIsJpg() {
		assertTrue(HtmlUtil.isJpg("sample.jpg"));
		assertTrue(HtmlUtil.isJpg("sample.JPG"));
		assertTrue(HtmlUtil.isJpg("sample.Jpg"));
		assertFalse(HtmlUtil.isJpg("sample.txt"));
	}

	@Test
	public void testIsPng() {
		assertTrue(HtmlUtil.isPng("sample.png"));
		assertTrue(HtmlUtil.isPng("sample.PNG"));
		assertTrue(HtmlUtil.isPng("sample.Png"));
		assertFalse(HtmlUtil.isPng("sample.txt"));
	}

	@Test
	public void testIsJpeg() {
		assertTrue(HtmlUtil.isJpeg("sample.jpeg"));
		assertTrue(HtmlUtil.isJpeg("sample.JPEG"));
		assertTrue(HtmlUtil.isJpeg("sample.Jpeg"));
		assertFalse(HtmlUtil.isJpeg("sample.txt"));
	}

	@Test
	public void testIsPicUrl() {
		assertTrue(HtmlUtil.isPicUrl("sample.bmp"));
		assertTrue(HtmlUtil.isPicUrl("sample.gif"));
		assertTrue(HtmlUtil.isPicUrl("sample.jpg"));
		assertTrue(HtmlUtil.isPicUrl("sample.png"));
		assertTrue(HtmlUtil.isPicUrl("sample.jpeg"));
		assertFalse(HtmlUtil.isPicUrl(null));
		assertFalse(HtmlUtil.isPicUrl(""));
		assertFalse(HtmlUtil.isPicUrl("sample.txt"));
	}
}
