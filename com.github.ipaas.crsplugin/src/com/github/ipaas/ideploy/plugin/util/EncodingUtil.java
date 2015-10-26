package com.github.ipaas.ideploy.plugin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

import com.github.ipaas.ideploy.plugin.bean.TreeNode;


/**
 * @author chenwj
 * 
 */
public class EncodingUtil {

	public static String MD2 = "MD2";
	public static String MD5 = "MD5";
	public static String SHA1 = "SHA-1";
	public static String SHA256 = "SHA-256";
	public static String SHA384 = "SHA-384";
	public static String SHA512 = "SHA-512";

	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * encode string
	 * 
	 * @param hashType
	 * @param str
	 * @return String
	 */
	public static String encodeString(String str, String hashType) {
		if (str == null) {
			return null;
		}
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(hashType);
			messageDigest.update(str.getBytes());
			return toHexString(messageDigest.digest());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// 加密文件
	public static String encodeFile(TreeNode node, File nodeFile,
			String hashType) {
		MessageDigest md5 = null;
		try {
			if (nodeFile.isDirectory())
				return encodeString(node.getRelativePath(), hashType);
			InputStream fis;
			fis = new FileInputStream(nodeFile);
			byte[] buffer = new byte[1024];
			md5 = MessageDigest.getInstance(hashType);
			int numRead = 0;
			while ((numRead = fis.read(buffer)) > 0) {
				md5.update(buffer, 0, numRead);
			}
			fis.close();
		} catch (Exception e) {

		}
		return encodeString(toHexString(md5.digest()) + node.getRelativePath(),
				hashType);
	}

	/**
	 * Takes the raw bytes from the digest and formats them correct.
	 * 
	 * @param bytes
	 *            the raw bytes from the digest.
	 * @return the formatted bytes.
	 */
	private static String toHexString(byte[] bytes) {
		int len = bytes.length;
		StringBuilder buf = new StringBuilder(len * 2);
		// 把密文转换成十六进制的字符串形式
		for (int j = 0; j < len; j++) {
			buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
			buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
		}
		return buf.toString();
	}

}