package com.github.ipaas.ideploy.plugin.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.github.ipaas.ideploy.plugin.bean.TreeNode;
import com.github.ipaas.ideploy.plugin.util.ConsoleHandler;

/**
 * 文件目录信息对比器
 * 
 * @author Chenql 
 */
public class FileTreeNodeComparetor {

	public static String ADD_CHART = "+";
	public static String DEL_CHART = "-";

	/**
	 * 对比两个文件目录,找出差异的文件或文件夹 返回 对比结果 +文件名 表示 src里添加或者更新过的文件(夹)
	 * 
	 * @param src
	 * @param target
	 * @return
	 */
	public static List<String> compare(TreeNode src, TreeNode target) {
		List<String> compareResult = new ArrayList<String>();
		ConsoleHandler.info("正在对比文件...");
		compare(src, target, compareResult);
		ConsoleHandler.info("文件对比结束.");
		return compareResult;
	}


	private static void compare(TreeNode src, TreeNode target, List<String> compareResult) {

		// System.out.println(src.getRelativePath());
		// 不是同一个目录
		if (!src.getRelativePath().equals(target.getRelativePath())) {
			return;
		}

		TreeSet<TreeNode> srcChildSet = src.getChildSet();
		TreeSet<TreeNode> targetChildSet = target.getChildSet();

		// 如果都有子集
		if (hasChildSet(src) && hasChildSet(target)) {
			for (TreeNode srcChildNode : srcChildSet) {

				for (TreeNode targetChildNode : targetChildSet) {
					if (srcChildNode.getRelativePath().equals(targetChildNode.getRelativePath())) { // 同一个文件(夹)
						if (srcChildNode.getNodeValue().equals(targetChildNode.getNodeValue())) {// 文件值相等
							break;
						} else {
							// todo
							if (!hasChildSet(srcChildNode)) {
								compareResult.add(new StringBuilder()
										.append(ADD_CHART + srcChildNode.getRelativePath()).toString());
							}
							compare(srcChildNode, targetChildNode, compareResult);
						}
					} // END 同一个文件(夹)
				}
			}
			// addSet增加src-target后的集合
			operate(difference(srcChildSet, targetChildSet), ADD_CHART, compareResult);
			// delSet增加target-src后的集合
			operate(difference(targetChildSet, srcChildSet), DEL_CHART, compareResult);

		} else if (hasChildSet(src)) {

			// 遍历src的全部子节点
			operate(srcChildSet, ADD_CHART, compareResult);
		} else if (hasChildSet(target)) {
			// 遍历target的全部子节点
			operate(targetChildSet, DEL_CHART, compareResult);
		}
	}

	private static TreeSet<TreeNode> difference(Set<TreeNode> set1, Set<TreeNode> set2) {
		TreeSet<TreeNode> tempSet = new TreeSet<TreeNode>();
		tempSet.clear();
		tempSet.addAll(set1);
		tempSet.removeAll(set2);
		return tempSet;
	}

	// 增加或删除一个目录下的文件
	private static void operate(TreeSet<TreeNode> tempSet, String operator, List<String> compareResult) {
		for (TreeNode node : tempSet) {
			compareResult.add(new StringBuilder().append(operator + node.getRelativePath()).toString());
			if (hasChildSet(node))
				operate(node.getChildSet(), operator, compareResult);
		}
	}

	public static boolean hasChildSet(TreeNode node) {
		return node.getChildSet() != null && node.getChildSet().size() != 0;
	}
}
