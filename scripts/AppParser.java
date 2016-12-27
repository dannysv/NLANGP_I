import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;


import java.io.StringReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.nio.charset.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.trees.HeadFinder;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.*; 
import edu.stanford.nlp.trees.TreeCoreAnnotations;

import com.google.common.base.Joiner;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.ling.LabeledWord;

class AppParser{
    private final static String PCG_MODEL = "../jars/englishPCFG.ser.gz";
    private final TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "invertible=true");
    private final LexicalizedParser parser = LexicalizedParser.loadModel(PCG_MODEL);

    private Tree parse(String str) {                
        List<CoreLabel> tokens = tokenize(str);
        Tree tree = parser.apply(tokens);
        return tree;
    }

    private List<CoreLabel> tokenize(String str) {
        Tokenizer<CoreLabel> tokenizer =
            tokenizerFactory.getTokenizer(
                new StringReader(str));    
        return tokenizer.tokenize();
    }

    public static void dfs(Tree node, Tree parent, HeadFinder headFinder) {
        String heads_words = "";
        if (node == null || node.isLeaf()) {
            return ;
        }
        //if node is a NP - Get the terminal nodes to get the words in the NP      
        if(node.value().equals("NP") ) {
            System.out.println(" Noun Phrase is ");
            List<Tree> leaves = node.getLeaves();

            for(Tree leaf : leaves) {
                System.out.print(leaf.toString()+" ");

            }
            System.out.println();

            System.out.println(" Head string is ");
            System.out.println(node.headTerminal(headFinder, parent));
            heads_words+=node.headTerminal(headFinder, parent).toString();
        }
        for(Tree child : node.children()) {
            dfs(child, node, headFinder);
        }
    }

    private static List<String> getNounPhrases(Tree parse) {
        List<String> result = new ArrayList<>();
        TregexPattern pattern = TregexPattern.compile("@NP");
        TregexMatcher matcher = pattern.matcher(parse);
        while (matcher.find()) {
            Tree match = matcher.getMatch();
            List<Tree> leaves = match.getLeaves();
            //System.out.println(leaves);
            // Some Guava magic.
            String nounPhrase = Joiner.on('_').join(Lists.transform(leaves, Functions.toStringFunction()));
            result.add(nounPhrase);
            List<LabeledWord> labeledYield = match.labeledYield();
            //System.out.println("labeledYield: " + labeledYield);
        }
        return result;
    }

    private static String getNounPhrases2(Tree parse) {
        String result = "headwords";
        TregexPattern pattern = TregexPattern.compile("@NP");
        TregexMatcher matcher = pattern.matcher(parse);
        while (matcher.find()) {
            Tree match = matcher.getMatch();
            List<Tree> leaves = match.getLeaves();
            //System.out.println(leaves);
            // Some Guava magic.
            String nounPhrase = Joiner.on('_').join(Lists.transform(leaves, Functions.toStringFunction()));
            result +=" ";
            result +=nounPhrase;
            //List<LabeledWord> labeledYield = match.labeledYield();
            //System.out.println("labeledYield: " + labeledYield);
        }
        return result;
    }

/*
    public static String dfs_headwords(Tree node, Tree parent, HeadFinder headFinder) {
        String heads_words = "";
        if (node == null || node.isLeaf()) {
            return "";
        }
        //if node is a NP - Get the terminal nodes to get the words in the NP      
        if(node.value().equals("NP") ) {
            List<Tree> leaves = node.getLeaves();
            //System.out.println(" Head string is ");
            String head_act = node.headTerminal(headFinder, parent).toString();
            heads_words = head_act;
        }

        for(Tree child : node.children()) {
            return  heads_words+" "+dfs(child, node, headFinder);
        }
    }
*/
    public static void main(String[] args){
        AppParser parser = new AppParser();
        Tree tree = parser.parse("Summary Spend your money elsewhere");
        //HeadFinder headfinder = new CollinsHeadFinder();
        //dfs(tree,null,headfinder);

        //List<String> noun_phrases = getNounPhrases(tree);
        //for(String np : noun_phrases){
        //    System.out.println(np);
        //}
        String resu = getNounPhrases2(tree);
        System.out.println(resu);
    }
}