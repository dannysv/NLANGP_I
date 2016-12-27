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
import edu.stanford.nlp.trees.TreeCoreAnnotations;

import com.google.common.base.Joiner;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.ling.LabeledWord;


class Parser{
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
        if (node == null || node.isLeaf()) {
            return;
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

        }

        for(Tree child : node.children()) {
         dfs(child, node, headFinder);
        }
    }

    private static String getNounPhrases2(Tree parse) {
        String result = "";
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
            result +=":1";
            //List<LabeledWord> labeledYield = match.labeledYield();
            //System.out.println("labeledYield: " + labeledYield);
        }
        return result;
    }


    public static void appParser_file(String str_path,String str_path_f,String str_path_categories){
        List<String> data = new ArrayList<> ();
        Set<String> categories = new HashSet<String>();
        Parser parser = new Parser(); 
        int cont = 0;
        try{
            BufferedReader br = new BufferedReader(new FileReader(str_path));
            String line = br.readLine();
            while(line!=null){
                //79|79:1|This computer is absolutely AMAZING!!!|LAPTOP#GENERAL|positive
                String[] b = line.split("\\|");
                String id_rev = b[0];
                String id_sent = b[1];
                String text = b[2];
                String category = b[3];
                String polarity = b[4];
                categories.add(category);
                Tree tree = parser.parse(text.replace(":",""));  
                //retrieve the Nounphrases
                String np_feat = getNounPhrases2(tree); 
                List<Tree> leaves = tree.getLeaves();
                //create the line to datatrain
                String line_data = "";
                // Print words and Pos Tags
                for (Tree leaf : leaves) { 
                    Tree parent = leaf.parent(tree);
                    
                    if(!parent.label().value().equals(".") &&
                       !parent.label().value().equals(",") &&
                       !parent.label().value().equals(":") &&
                       !parent.label().value().equals(";") &&
                       !parent.label().value().equals("-LRB-") &&
                       !parent.label().value().equals("-RRB-") &&
                       !leaf.label().value().equals(".") &&
                       !leaf.label().value().equals(",") &&
                       !leaf.label().value().equals(":") &&
                       !leaf.label().value().equals(";")){
                        //System.out.print(leaf.label().value() + "-" + parent.label().value() + " ");
                        
                        line_data += " ";
                        line_data += leaf.label().value();
                        line_data += ":1";
                    }
                }
                cont++;
                System.out.println(cont);
                //System.out.println(id+"|"+line_data);
                String line_f = id_rev+"|"+id_sent+"|"+line_data+"|"+category+"|"+polarity+"|"+np_feat;
                data.add(line_f);
                //System.out.println();
                line = br.readLine();
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        Path out = Paths.get(str_path_f);
        try{
            Files.write(out,data,Charset.defaultCharset());
        }catch(Exception e){
            System.out.println(e);
        }
        Path out2 = Paths.get(str_path_categories);
        try{
            Files.write(out2,categories,Charset.defaultCharset());
        }catch(Exception e){
            System.out.println(e);
        }
    }



	public static void main(String[] args){
		String str_path_train = "../data/csv_train.csv";
        String str_path_train_f = "../data/data_categories/vp/csv_train_parse.csv";
        String str_path_train_categories = "../data/data_categories/train_categories";

		String str_path_test = "../data/csv_test.csv";
        String str_path_test_f = "../data/data_categories/vp/csv_test_parse.csv";
        String str_path_test_categories = "../data/data_categories/test_categories";

        appParser_file(str_path_train,str_path_train_f,str_path_train_categories);
        appParser_file(str_path_test,str_path_test_f,str_path_test_categories);
	}
}