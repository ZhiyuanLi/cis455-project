package edu.upenn.cis455.xpathengine;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;

import org.w3c.dom.NamedNodeMap;

public class XPathEngineImpl implements XPathEngine {

	public List<String> xPaths = new ArrayList<String>();
	private List<Node> copy = new ArrayList<Node>();

	// list of regex for matching
	private String regex = "\\s*([A-Z_a-z][A-Z_a-z-.0-9]*)" + "\\s*(\\[.+\\])*";
	private String textRegex = "\\s*text\\(\\)\\s*" + "=" + "\\s*\"(.*)\"\\s*";
	private String containsRegex = "\\s*contains\\s*\\" + "(\\s*text\\(\\)\\s*," + "\\s*\"(.*)\"\\s*\\)\\s*";
	private String attributeRegex = "\\s*@([^\\s]+)\\s*" + "=" + "\\s*\"(.*)\"\\s*";

	// Do NOT add arguments to the constructor!!
	public XPathEngineImpl() {

	}

	/**
	 * Store the XPath expressions that are given to this method
	 */
	public void setXPaths(String[] s) {
		for (String eachPath : s) {
			xPaths.add(eachPath.trim());
		}
	}

	// ********* Part 1: Check Valid ***************

	/**
	 * given index i check if ith xpath is valid
	 */
	public boolean isValid(int i) {
		if (i >= xPaths.size()) {
			return false;
		}
		String path = xPaths.get(i);

		if (path == null) {
			return false;
		}
		if (path.contains("http://")) {
			path = "/" + path.split("//")[1];
		}
		return isValidSeperate(path);
	}

	/**
	 * helper function to check if a single xpath is valid
	 * 
	 * @param path
	 * @return
	 */
	private boolean isValidSeperate(String path) {
		List<String> pathSteps = separate(path);
		if (pathSteps == null)
			return false;
		for (String eachStep : pathSteps) {
			// System.out.println("each step is:" + eachStep);
			if (!validStep(eachStep)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * separate a path by slash, such as /aa/bb/cc to List<String>{aa, bb, cc}
	 * 
	 * @param path
	 * @return
	 */
	private List<String> separate(String path) {
		Stack<Character> stack = new Stack<Character>(); // to store brackets
		List<String> tokens = new ArrayList<String>(); // inside are steps
		if (!path.contains("/")) {
			tokens.add(path);
			return tokens;
		}

		boolean inQuote = false;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < path.length(); i++) {
			char ch = path.charAt(i);
			// case 0
			if (ch == ' ') {// ignore white spaces
				if (!inQuote) {
					continue;
				}
			}
			// case 1
			if (ch == '/' && stack.isEmpty() && !inQuote) {
				if (sb.length() > 0) {
					// add one step between two slash
					tokens.add(sb.toString().trim());
					sb = new StringBuilder();
				}
			}
			// case 2
			else if (ch == '"') {
				sb.append(ch);
				inQuote = !inQuote;
			}
			// case 3
			else if (ch == '[') {
				sb.append(ch);
				if (!inQuote) {
					stack.push(ch);
				}
			}
			// case 4
			else if (ch == ']') {
				if (stack.isEmpty()) {
					return null;
				}
				sb.append(ch);
				if (!inQuote) {
					stack.pop();
				}
			}
			// case 5
			else {
				sb.append(ch);
			}
		}
		// error checking
		if (!stack.isEmpty()) {
			return null;
		}
		// add last step
		if (sb.length() > 0) {
			tokens.add(sb.toString());
		}
		return tokens;
	}

	private boolean validStep(String step) {
		// System.out.println("step is: " + step);

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(step);

		if (matcher.matches()) {
			Stack<Character> stack = new Stack<Character>();
			StringBuilder sb = new StringBuilder();
			boolean quote = false;
			boolean inProcess = false;
			for (int i = 0; i < step.length(); i++) {
				char ch = step.charAt(i);
				// case 1
				if (ch == '"') {
					quote = !quote;
				}
				// case 2
				else if (ch == '[') {
					if (!quote) {
						stack.push(ch);
						inProcess = true; // start to append
					}
				}
				// case 3
				else if (ch == ']') {
					if (!quote) {
						if (stack.isEmpty()) { // error
							return false;
						}
						stack.pop();
						if (stack.isEmpty()) {
							sb.append(ch); // append right brackets
							inProcess = false;
							if (!isMatch(sb.toString().trim().substring(1, sb.length() - 1))) { // with
																								// brackets
								return false;
							}
							sb = new StringBuilder();
						}
					}
				}
				if (inProcess) {
					sb.append(ch);
				}
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * test if given step match any of text, contains, or attribute pattern, if
	 * not, call recursively
	 * 
	 * @param step
	 * @return
	 */
	private boolean isMatch(String step) {
		// borrowed from tutorial

		Pattern textPattern = Pattern.compile(textRegex);
		Matcher textmatcher = textPattern.matcher(step);

		Pattern containsPatern = Pattern.compile(containsRegex);
		Matcher containMatcher = containsPatern.matcher(step);

		Pattern attributePattern = Pattern.compile(attributeRegex);
		Matcher attributeMatcher = attributePattern.matcher(step);

		if (textmatcher.matches() || containMatcher.matches() || attributeMatcher.matches()) {
			return true;
		} else {
			return isValidSeperate(step);
		}
	}

	// ********* Part 2: Evaluate xpath ***************

	/**
	 * evaluate each xpath given is valid or not, return a boolean array
	 */
	public boolean[] evaluate(Document d) {
		if (d == null) {
			return new boolean[xPaths.size()];
		}
		List<Node> children = new ArrayList<Node>();
		// to reset children in each for loop
		List<Node> childrenCopy = new ArrayList<Node>();
		children.add(d.getDocumentElement());
		childrenCopy = children;

		boolean[] result = new boolean[xPaths.size()];
		for (int i = 0; i < xPaths.size(); i++) {
			children = childrenCopy;
			if (isValid(i)) {
				List<String> pathSteps = separate(xPaths.get(i));
				if (pathSteps == null || pathSteps.size() == 0) {
					result[i] = false;
					continue;
				}
				// need to satisfy each level, if one level not satisfy, return
				// false
				for (int level = 0; level < pathSteps.size(); level++) {
					if (evaluateEachStep(pathSteps.get(level), children)) {
						children = copy;
						if (level == pathSteps.size() - 1) {
							copy = new ArrayList<Node>();
							result[i] = true;
						}
					} else {
						result[i] = false;
						break;
					}
				}
			} else {
				result[i] = false;
			}
		}
		return result;
	}

	/**
	 * recursive step, given a node list and a step, check if this step is
	 * satisfied
	 * 
	 * @param step
	 * @param nodeList
	 * @return
	 */
	private boolean evaluateEachStep(String step, List<Node> nodeList) {
		// System.out.println("each step is:" + step);
		// case 1: no brackets
		if (!step.contains("[")) { // no brackets
			for (Node node : nodeList) {
				// if one of node in nodelist matches, return true
				if (step.trim().equals(node.getNodeName())) {
					NodeList tempList = node.getChildNodes();
					copy = nodeListToArrayList(tempList);
					return true;
				}
			}
		}
		// case 2: have brackets
		else {
			// like a[bb][cc][dd] -> first + second + third
			String first = step.substring(0, step.indexOf("["));
			Stack<Character> stack1 = new Stack<Character>();
			String afterFirst = step.substring(step.indexOf("["));
			String afterSecond = null;
			String second = "";
			for (int i = 0; i < afterFirst.length(); i++) {
				char ch = afterFirst.charAt(i);
				// to figure out the second element
				if (ch == '[') {
					stack1.push('[');
				} else if (ch == ']') {
					if (stack1.empty()) {
						return false;
					}
					stack1.pop();
					if (stack1.empty()) {
						second = afterFirst.substring(1, i);
						// System.out.println("Second is: " + second);
						break;
					}
				}
			}
			afterSecond = afterFirst.substring(1 + second.length() + 1);
			// System.out.println("afterSecond is: " + afterSecond);

			// case 2-1: have more than one test
			if (!(afterSecond == null || afterSecond.length() == 0)) {
				// like aa[bb][cc] or aa[bb][cc][dd]...
				Stack<Character> stack2 = new Stack<Character>();
				List<String> allTests = new ArrayList<String>();
				String third = ""; // rest of third element
				allTests.add(second);// add second first
				for (int i = 0; i < afterSecond.length(); i++) {
					char ch = afterSecond.charAt(i);
					if (ch == '[') {
						if (stack2.empty()) {
							// ignore first open bracket
						} else {
							third += ch;
						}
						stack2.push('[');
					} else if (ch == ']') {
						if (stack2.empty()) {
							return false;
						}
						stack2.pop();
						if (!stack2.empty()) {
							// ignore last close bracket
							third += ch;
						} else {
							allTests.add(third);
							third = "";
						}
					} else {
						third += ch;
					}
				}
				// same as case 1
				for (Node node : nodeList) {
					if (first.trim().equals(node.getNodeName())) {
						NodeList secondChildList = node.getChildNodes();
						List<Node> secondChildren = nodeListToArrayList(secondChildList);
						// check each test, must all satisfy!
						int count = 0;
						for (String eachTest : allTests) {
							// System.out.println("## " + eachTest);
							if (!eachTest.contains("[")) {
								if (isElement(eachTest, secondChildren) || isText(eachTest, node)
										|| isContains(eachTest, node) || isAttribute(eachTest, node)) {
									count++;
								}
							} else {
								// search third level
								if (evaluateEachStep(eachTest, secondChildren))
									count++;
							}
							// System.out.println("count is " + count);
							// meaning all tests are satisfied
							if (count == allTests.size()) {
								return true;
							}
						}
					}
				}
			}
			// case 2-2: only have one test
			else {
				// like aa[bb[cc]] or aa[bb]
				for (Node node : nodeList) {
					if (first.trim().equals(node.getNodeName())) { // first
																	// level
																	// match
						NodeList secondChildList = node.getChildNodes();
						List<Node> secondChildren = nodeListToArrayList(secondChildList);
						if (second.contains("[")) {
							// recursive solve second element!
							// System.out.println("second element has
							// brackets");
							return evaluateEachStep(second, secondChildren);
						} else {
							if (isElement(second, secondChildren) || isText(second, node) || isContains(second, node)
									|| isAttribute(second, node)) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	// ********* Part 3: Helper function ************

	/**
	 * helper funntion to convert nodelist to array of nodes, used in
	 * evaluateEachStep
	 * 
	 * @param nodeList
	 * @return
	 */
	private List<Node> nodeListToArrayList(NodeList nodeList) {
		List<Node> result = new ArrayList<Node>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			result.add(nodeList.item(i));
		}
		return result;
	}

	/**
	 * check if node is a text
	 * 
	 * @param step
	 * @param node
	 * @return
	 */
	private boolean isText(String step, Node n) {
		String text = "";
		Pattern textPattern = Pattern.compile(textRegex);
		Matcher textMatcher = textPattern.matcher(step);

		if (textMatcher.matches()) {
			text = textMatcher.group(1);
			if (n.getTextContent() != null && n.getTextContent().trim().equals(text.trim())) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * check if node is a contain
	 * 
	 * @param step
	 * @param node
	 * @return
	 */
	private boolean isContains(String step, Node n) {
		String contain = "";
		Pattern containPattern = Pattern.compile(containsRegex);
		Matcher containMatcher = containPattern.matcher(step);

		if (containMatcher.matches()) {
			contain = containMatcher.group(1);
			if (n.getTextContent() != null && n.getTextContent().trim().contains(contain.trim())) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * check if node is a attribute
	 * 
	 * @param step
	 * @param node
	 * @return
	 */
	private boolean isAttribute(String step, Node n) {
		Pattern attributePattern = Pattern.compile(attributeRegex);
		Matcher attributeMatcher = attributePattern.matcher(step);
		String content = "";
		String attribute = "";
		if (attributeMatcher.matches()) {
			attribute = attributeMatcher.group(1);
			content = attributeMatcher.group(2);
			NamedNodeMap nameNode = n.getAttributes();
			if (nameNode != null) {
				Node value = nameNode.getNamedItem(attribute);
				if (value != null && value.getNodeValue().trim().equals(content.trim())) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * check if node is an element
	 * 
	 * @param step
	 * @param nodes
	 * @return
	 */
	private boolean isElement(String step, List<Node> nodeList) {
		String[] pathSplitBySlash = step.split("/");

		for (int level = 0; level < pathSplitBySlash.length; level++) {
			for (Node node : nodeList) {
				if (pathSplitBySlash[level].trim().equals(node.getNodeName())) {
					if ((level + 1) == pathSplitBySlash.length) {
						return true;
					}
					NodeList list = node.getChildNodes();
					nodeList = nodeListToArrayList(list);
					break;
				}
			}
		}
		return false;
	}
}