package com.allendowney.thinkdast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

public class WikiPhilosophy {

    final static List<String> visited = new ArrayList<String>();
    final static WikiFetcher wf = new WikiFetcher();
    
    /**
     * Tests a conjecture about Wikipedia and Philosophy.
     *
     * https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
     *
     * 1. Clicking on the first non-parenthesized, non-italicized link
     * 2. Ignoring external links, links to the current page, or red links
     * 3. Stopping when reaching "Philosophy", a page with no links or a page
     *    that does not exist, or when a loop occurs
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String destination = "https://en.wikipedia.org/wiki/Philosophy";
        String source = "https://en.wikipedia.org/wiki/Java_(programming_language)";

        testConjecture(destination, source, 15);
    }

    /**
     * Starts from given URL and follows first link until it finds the destination or exceeds the limit.
     *
     * @param destination
     * @param source
     * @throws IOException
     */
    public static void testConjecture(String destination, String source, int limit) throws IOException {
        String result = "";
    	int linksFollowed = 0;
    	String currentLink = source;
    	boolean validLinkFound = false;
    	
    	do {
    		if (visited.contains(currentLink)) {
    			result = "FAILURE - Already visited link: " + currentLink;
    			break;
    		}
    		
    		if (currentLink.equals(destination)) {
    			result = "SUCCESS - Reached destination: " + destination + ". Links followed: " + linksFollowed;
    			break;
    		}
    		
        	Elements elements = wf.fetchWikipedia(currentLink);
        	validLinkFound = false;
    		visited.add(currentLink);

        	for (Element element: elements) {
        		Iterable<Node> iterable = new WikiNodeIterable(element);
            	
            	for (Node node: iterable) {
            		if (isValidLink(node, element)) {
            			String concreteURL = createConcreteURL(node);
            			
            			if (!concreteURL.equals(currentLink)) {
            				currentLink = concreteURL;
            				validLinkFound = true;
            				linksFollowed += 1;
                			break;
            			}
            		}
            	}
            	
            	if (validLinkFound) {
            		break;
            	}
        	}
    	}
    	while (validLinkFound && linksFollowed <= limit);
    	
    	if (!validLinkFound) {
    		result = "FAILURE - No valid link found on: " + currentLink;
    	}
    	
    	if (linksFollowed > limit) {
    		result = "FAILURE - Links followed: " + linksFollowed;
    	}
    	
    	System.out.println("Links Visited:");
    	for (String link: visited) {
    		System.out.println("\t" + link);
    	}
    	System.out.println(result);
    }
    
    private static boolean isValidLink(Node node, Node root) {
    	boolean isValidLink = isRelativeLink(node);
    	
    	if (isValidLink) {
    		isValidLink = !isInItalics(node, root);
    	}
    	
    	if (isValidLink) {
    		isValidLink = !isWithinParenthesis(node, root);
    	}
    	    	
    	return isValidLink;
    }
    
    private static boolean isRelativeLink(Node node) {
    	boolean isLink = (node instanceof Element && ((Element)node).tagName() == "a");
    	
    	return isLink ? node.attributes().get("href").startsWith("/wiki") : false;
    }

	private static boolean isInItalics(Node node, Node root) {
		Node parentNode = node.parentNode();
		
		while (parentNode != root) {
			
			if (parentNode instanceof Element) {
				Element parentElement = (Element) parentNode;
				if (parentElement.tagName() == "em" || parentElement.tagName() == "i") {
					return true;
				}
			}
			
			parentNode = parentNode.parentNode();
		}
		
		return false;
	}
	
	private static boolean isWithinParenthesis(Node node, Node root) {
		Node parentNode = node.parentNode();
		
		while (parentNode != root) {
			parentNode = parentNode.parentNode();
		}
		
		String parentNodeStr = parentNode.toString();
		int linkStart = parentNodeStr.indexOf(node.toString());
		
		int left = countBrackets(0, linkStart, parentNodeStr);
		int right = countBrackets(linkStart + node.toString().length(), parentNodeStr.length(), parentNodeStr) * -1;
		
		return (left > 0 && right > 0 && left == right);
	}
    
    private static int countBrackets(int start, int end, String str) {
    	int brackets = 0;
    	
		for (int i = start; i < end; i++) {
			char c = str.charAt(i);
			if (c == '(') {
				brackets += 1;
			}
			else if (c == ')') {
				brackets -= 1;
			}
		}
		
		return brackets;
	}

	private static String createConcreteURL(Node node) {
    	String URL = node.attributes().get("href");
    	
    	return "https://en.wikipedia.org" + URL;
    }
}
