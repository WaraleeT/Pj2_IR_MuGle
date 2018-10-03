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
	
	public TFIDFSearcher(String docFilename) {
		super(docFilename);
		/************* YOUR CODE HERE ******************/
		
		for(Document d: super.documents){
			vocab.addAll(d.getTokens());
		}
		System.out.println(vocab.size());
		
		for(String q : vocab){
			//Find idf
			int countDoc = 0;
			for(Document d : super.documents){
				if(d.getTokens().contains(q)){
					countDoc++;
				}
			}
//			System.out.println(q+"   =   "+countDoc);
			double idf = Math.log(1+(super.documents.size()/(double)countDoc));
			idfscore.put(q, idf);
			
		}
		
//		for(String key : idfscore.keySet()){
//			System.out.println(key+" : "+idfscore.get(key));
//		}
//		System.out.println(idfscore.size());
			
		
		/***********************************************/
	}
	
	@Override
	public List<SearchResult> search(String queryString, int k) {
		/************* YOUR CODE HERE ******************/
		
		List<SearchResult> result = new ArrayList<SearchResult>();
		Map<String, Double> docVector = new HashMap<>();
		Map<String, Double> qVector = new HashMap<>();		
		
		//Tokenize query
		List<String> query = super.tokenize(queryString);
		List<String> queryList = new ArrayList<>();
		for(String str: query){
			if(!queryList.contains(str)){
				queryList.add(str);
				vocab.add(str);
			}
		}
		
		for(String str:queryList){
			int count = 0;
			for(Document d: super.documents){
				if(d.getTokens().contains(str)){
					count++;
				}
			}
			double idf = Math.log(1+(super.documents.size()/(double)count));
			idfscore.put(str, idf);
		}
		
		for(String q:vocab){
			int count = 0;
			double tf = 0;
			double weight = 0;
			count = Collections.frequency(queryList, q);
//			for(String str:queryList){
//				if(str.equals(q)){
//					count++;
//				}
//			}
			if(count == 0){
				tf = 0;
			}
			else{
				tf = 1 + Math.log(count);
			}
			weight = tf*idfscore.get(q);
			qVector.put(q, weight);
		}
		
		for(String i : qVector.keySet()){
			System.out.println(i+" "+qVector.get(i));
		}
				
		
		for(Document d : super.documents){
			double weight = 0;
			for(String q : vocab){
				int count = 0;
				count = Collections.frequency(d.getTokens(), q);
//				for(String s: d.getTokens()){
//					if(s.equals(q)){
//						count++;
//					}
//				}
				System.out.println("Doc"+d.getId()+" Query "+q+" : "+count);
				double tfscore;
				if(count == 0){
					tfscore = 0;
				}
				else{
					tfscore = 1 + Math.log(count);
				}
				weight = tfscore*idfscore.get(q);
				docVector.put(q, weight);
				System.out.println("Doc"+d.getId()+" Query "+q+"TF score : "+tfscore+" idfscore: "+idfscore.get(q) +" weight : "+weight);
				
			}
			double score = 0;
			double sized = 0;
			double sizeq = 0;
			for(String term:vocab){
				score += docVector.get(term)*qVector.get(term);
				sized += Math.pow(docVector.get(term),2);
				sizeq += Math.pow(qVector.get(term),2);
			}
			score = score/(Math.sqrt(sized)+Math.sqrt(sizeq));	
			SearchResult docResult = new SearchResult(d, score);
			result.add(docResult);
			break;
		}
		
		
		Collections.sort(result);
		
		return result.subList(0, k);
		/***********************************************/
	}
}
