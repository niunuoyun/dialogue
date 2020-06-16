package com.example.dialogue.tries;

import lombok.extern.slf4j.Slf4j;
import org.nlpcn.commons.lang.util.StringUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Huihua Niu
 * on 2020/6/5 12:00
 */
@Slf4j
public class TrieTree {
    private TrieNode root = new TrieNode();

    class TrieNode {
        TrieNode preNode = null;
        boolean isEnd = false;//是否是红点，也就是是否是word的结尾
        int deep = 0;//做hash使用，防止一个单词里面有多个char的时候hash是一样的，可能导致删除出错
        char content = 0;//当前节点到parent节点存储的字母
        Map<String,TrieNode> child;
        String atrb;//标准句式转换
        String featureWord; //词性特征
        TrieNode() {
            child = new HashMap<>();
        }

        @Override
        public String toString() {
            return "\n" + "{" +
                    "End=" + isEnd +
                    ", d=" + deep +
                    ", c=" + content +
                    ", c=" + child +
                    '}';
        }

        @Override
        public int hashCode() {
            return content + deep;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof TrieNode && (((TrieNode) obj).content == content);
        }

        void setPreNode(TrieNode node) {
            preNode = node;
        }

        TrieNode getPreNode() {
            return preNode;
        }

        /**
         * child中删掉某个Node
         *
         * @param node 需要删掉的node
         */
        void removeChild(TrieNode node) {
            for (TrieNode aChild : child.values()) {
                if (aChild.content == node.content) {
                    child.remove(aChild);
                    break;
                }
            }
        }

        /**
         * child中是否有此Node
         *
         * @param character 保存的char
         * @return 存在返回不存在返回Null
         */
        TrieNode getNode(Character character) {
            for (TrieNode aChild : child.values()) {
                if (aChild.content == character) {
                    return aChild;
                }
            }
            return null;
        }
    }

    /**
     * 添加一个word
     * apple
     *
     * @param word 需要添加的词
     */
    public void addWord(String word,String atrb,String featureWord) {
        int deep = 0;
        TrieNode currNode = root;
        while (deep < word.length()) {
            /*
             * 判断当前node的child，如果为空直接添加，不为空，查找是否含有，不含有则添加并设为currNode，含有则找到并设置为currNode
             */
            char c = word.charAt(deep);
            if (currNode.child.get(word)!=null) {
                currNode = currNode.getNode(c);
            } else {
                TrieNode node = new TrieNode();
                node.setPreNode(currNode);
                node.deep = deep + 1;
                currNode.child.put(String.valueOf(c),node);
                currNode = node;
            }
            if (deep == word.length() - 1) {
                currNode.isEnd = true;
                if (StringUtil.isNotBlank(atrb)) currNode.atrb = atrb;
                if (StringUtil.isNotBlank(featureWord)) currNode.featureWord = featureWord;
            }
            deep++;
        }
    }

    /**
     * word在map中是否存在
     *
     * @param word 需要查找的word
     * @return 是否存在
     */
    public boolean hasWord(String word) {
        int deep = 0;
        TrieNode currNode = root;
        while (deep < word.length()) {
            char c = word.charAt(deep);
            if (Objects.nonNull(currNode.child.get(c))) {
                currNode = currNode.getNode(c);
            } else {
                return false;
            }
            if (deep == word.length() - 1) {
                return currNode.isEnd;
            }
            deep++;
        }
        return false;
    }

    /**
     * 移除word，几种情况：
     * 1、word在list中不存在，直接返回失败
     * 2、word最后一个char 没有child，则删掉此节点并朝 root 查找没有child && isEnd=false 的节点都删掉
     * 3、word最后一个char 有child，则把isEnd置为false
     *
     * @param word 需要移除的word
     * @return 是否移除成功
     */
    public boolean removeWord(String word) {
        if (word == null || word.trim().equals("")) {
            return false;
        }
        if (hasWord(word)) {
            return false;
        }
        int deep = 0;
        TrieNode currNode = root;
        while (deep < word.length()) {
            char c = word.charAt(deep);
            if (Objects.nonNull(currNode.child.get(c))) {
                currNode = currNode.getNode(c);
            } else {
                return false;
            }
            if (deep == word.length() - 1) {
                if (currNode.child.size() > 0) {
                    //3、word最后一个char 有child，则把isEnd置为false
                    currNode.isEnd = false;
                    return true;
                } else {
                    //2、word最后一个char 没有child，则删掉此节点并朝 root 查找没有child && isEnd=false 的节点都删掉
                    TrieNode parent = currNode.getPreNode();
                    while (parent != null) {
                        if (parent.child.size() == 0 && !parent.isEnd) {
                            parent.removeChild(currNode);
                            currNode = parent;
                        } else {
                            return true;
                        }
                    }
                }
            }
            deep++;
        }

        return false;
    }

    /**
     * 前序遍历所有节点
     */
    public void traverseTree() {
        visitNode(root, "");
    }

    private void visitNode(TrieNode node, String result) {
        log.info("node.content->" + node.content);
        String re = result + node.content;
        for (TrieNode n : node.child.values()) {
            visitNode(n, re);
            log.info("result->" + re);
        }
    }


    public static void main(String[] args) {


    }
}
