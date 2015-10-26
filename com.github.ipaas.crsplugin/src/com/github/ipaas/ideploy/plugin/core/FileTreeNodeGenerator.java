 
package com.github.ipaas.ideploy.plugin.core;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeSet;

import com.github.ipaas.ideploy.plugin.bean.TreeNode;
import com.github.ipaas.ideploy.plugin.util.ConsoleHandler;
import com.github.ipaas.ideploy.plugin.util.EncodingUtil;

/**
 * 文件系统信息生成器
 * 
 * @author Chenql  
 */
public class FileTreeNodeGenerator {

	private static String prefixPath;// 文件系统前缀

	public static TreeNode buildNode(String absoluteFilePath) {
		if (absoluteFilePath == null || absoluteFilePath.equals("")) {
			return null;
		}
		prefixPath = absoluteFilePath;
		ConsoleHandler.info("prefixPath:" + prefixPath);
		TreeNode rootNode = new TreeNode();
		rootNode.setRelativePath("");
		ConsoleHandler.info("正在生成本地文件信息...");
		buildNode(rootNode);
		ConsoleHandler.info("文件信息生成结束.");
		return rootNode;
	}

	private static void buildNode(TreeNode node) {

		TreeSet<TreeNode> childSet = genChildSet(node);

		node.setChildSet(childSet);// 下级文件/文件夹信息
		if (childSet == null)
			return;
		StringBuilder builder = new StringBuilder();
		for (TreeNode tempNode : childSet) {
			builder.append(tempNode.getNodeValue());
		}
		File nodeFile = new File(prefixPath + node.getRelativePath());
		if (!nodeFile.exists())
			return;
		builder.append(EncodingUtil.encodeFile(node, nodeFile, EncodingUtil.SHA1));
		node.setNodeValue(EncodingUtil.encodeString(builder.toString(), EncodingUtil.SHA1));
	}

	/**
	 * @param node
	 *            对文件夹节点 生成文件节点的子目录及文件信息,对文件节点生成文件信息 过滤了 文件名 以 "."开头的项目配置文件
	 *            文件分隔符统一换成 /
	 */
	public static TreeSet<TreeNode> genChildSet(TreeNode node) {
		File nodeFile = new File(prefixPath + node.getRelativePath());
		if (!nodeFile.exists()) {
			ConsoleHandler.error("File no exist:" + nodeFile.getAbsolutePath());
			return null;
		}

		String nodeValue = EncodingUtil.encodeFile(node, nodeFile, EncodingUtil.SHA1);
		if (nodeFile.isFile()) {// 如果是文件,计算文件值
			node.setNodeValue(nodeValue);
			return null;
		} else {// 如果是文件夹,计算每个子文件/文件夹
			File[] fileList = nodeFile.listFiles();
			// 对文件按文件名排序
			Arrays.sort(fileList, new Comparator<File>() {
				@Override
				public int compare(File file1, File file2) {
					return file1.getName().compareTo(file2.getName());
				}
			});

			TreeSet<TreeNode> childSet = new TreeSet<TreeNode>();
			for (File file : fileList) {
				TreeNode childNode = new TreeNode();
				// 为了兼容window下的目录,加上盘符的长度,计算前缀目录在absolutePath的起始位置,window下为2,linux下为0
				int driveLength = file.getAbsolutePath().replaceAll("\\\\", "/")
						.indexOf(prefixPath.replaceAll("\\\\", "/"));

				String relativePath = file.getAbsolutePath()
						.substring(prefixPath.length() + driveLength, file.getAbsolutePath().length())
						.replaceAll("\\\\", "/");
				String fileName = file.getAbsolutePath().substring(
						file.getAbsolutePath().lastIndexOf(File.separator) + 1, file.getAbsolutePath().length());
				if (relativePath == null || fileName.startsWith(".")) { // 过滤
																		// "."开头的项目配置文件
					continue;
				}
				childNode.setRelativePath(relativePath);
				buildNode(childNode);
				childSet.add(childNode);
			}
			return childSet;
		}
	}
}
