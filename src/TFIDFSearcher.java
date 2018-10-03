//Name: 
//Section: 
//ID: 

import java.util.ArrayList;
import java.util.Collections;
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
			double idf = Math.log(1+(super.documents.size()/countDoc));
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
		
		//Tokenize query
		List<String> query = super.tokenize(queryString);
		List<String> queryList = new ArrayList<>();
		for(String str: query){
			if(!queryList.contains(str)){
				queryList.add(str);
			}
		}
				
		List<SearchResult> result = new ArrayList<SearchResult>();

		for(Document d : super.documents){
			double score = 0;
			for(String q : d.getTokens()){
				int count = 0;
				for(String s: d.getTokens()){
					if(s.equals(q)){
						count++;
					}
				}
				System.out.println("Doc"+d.getId()+" Query "+q+" : "+count);
				double tfscore;
				if(count == 0){
					tfscore = 0;
				}
				else{
					tfscore = 1 + Math.log(count);
				}
				score = tfscore*idfscore.get(q);
				System.out.println("Doc"+d.getId()+" Query "+q+"TF score : "+tfscore+" idfscore: "+idfscore.get(q) +" score : "+score);
			}
			
			SearchResult resultScore = new SearchResult(d, score);
			result.add(resultScore);
			break;
		}
		
		Collections.sort(result);
		
		
		
		return result.subList(0, k);
		/***********************************************/
	}
}
