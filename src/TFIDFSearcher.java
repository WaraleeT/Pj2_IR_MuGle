//Name: 
//Section: 
//ID: 

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.plaf.synth.SynthSplitPaneUI;

public class TFIDFSearcher extends Searcher
{	public Map<String, Double> idfscore = new TreeMap();
	public Set<String> vocab = new HashSet<String>();
	public Map<Document, TreeMap<String, Double>> tfscore = new TreeMap();
	public TFIDFSearcher(String docFilename) {
		super(docFilename);
		/************* YOUR CODE HERE ******************/
		//Add all tokens of every document to vocab
		for(Document d: super.documents){
			vocab.addAll(d.getTokens());
		}
		
		for(String q : vocab){
			//Find idf
			int countDoc = 0;
			for(Document d : super.documents){
				if(d.getTokens().contains(q)){
					countDoc++;
				}
			}
			
			double idf = Math.log10(1+(super.documents.size()/(double)countDoc));
			idfscore.put(q, idf);
		}
		
		/***********************************************/
	}
	
	@Override
	public List<SearchResult> search(String queryString, int k) {
		/************* YOUR CODE HERE ******************/
		
		List<SearchResult> result = new ArrayList<SearchResult>();
		Map<String, Double> docVector = new TreeMap<>();
		Map<String, Double> qVector = new TreeMap<>();		
		
		//Tokenize query
		List<String> query = super.tokenize(queryString);
		List<String> queryList = new ArrayList<>();
		for(String str: query){
			queryList.add(str);
			vocab.add(str);
		}
		
		//Add new idf for query that doesn't exist
		//(If it doesn't exist in vocab before, there is non of them in corpus)
		for(String str:queryList){
			if(idfscore.get(str)==null){
				idfscore.put(str, 0.0);
			}
		}
		
		//Create Map that contains weight of query
		//Length = all vocab (Query vector need to do dot product with all document)
		for(String q:vocab){
			int count = 0;
			double tf = 0;
			double weight = 0;
			count = Collections.frequency(queryList, q);
			if(count == 0){
				tf = 0;
			}
			else{
				tf = 1 + Math.log10(count);
			}
			weight = tf*idfscore.get(q);
			qVector.put(q, weight);
		}		
		
		//Find score of each document
		for(Document d : super.documents){
			double weight = 0;
			double score = 0;
			double sized = 0;
			double sizeq = 0;
			Set<String> unionSet = new HashSet<>();
			
			//Union query and document
			unionSet.addAll(d.getTokens());
			unionSet.addAll(queryList);
			
			for(String q : unionSet){
				int count = 0;
				count = Collections.frequency(d.getTokens(), q);
				double tfscore;
				if(count == 0){
					tfscore = 0;
					weight = 0;
				}
				else{
					tfscore = 1 + Math.log10(count);
					
				}
				weight = tfscore*idfscore.get(q);
				score += weight*qVector.get(q);
				sized += Math.pow(weight,2);
				sizeq += Math.pow(qVector.get(q),2);
			}
			
			score = score/(Math.sqrt(sized)*Math.sqrt(sizeq));	
			SearchResult docResult = new SearchResult(d, score);
			result.add(docResult);
	
		}
		
		
		Collections.sort(result);
		
		return result.subList(0, k);
		/***********************************************/
	}
}
