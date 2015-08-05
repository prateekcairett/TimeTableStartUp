package Permute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class SpacedOut {
	private HashSet<String> m_permute_memory;
	private HashMap<String, Double> m_ranked_timetable;
	private Map<Integer, String> m_subject_code;
	
	private Queue<Double> m_best_combinatorial_score = new PriorityQueue<>(10); 
	private Map<String, Double> m_dynamic_scores;
	
	public SpacedOut(){
		m_permute_memory = new HashSet<String>();
		m_ranked_timetable = new HashMap<String, Double>();
		m_subject_code = new HashMap<Integer, String>();
		m_ranked_timetable.clear();
		m_best_combinatorial_score.clear();
		m_dynamic_scores.clear();
	}
	
	public Double MinScoreInBestScores(){
		return m_best_combinatorial_score.peek();
	}
	
	public Double DynamicScore(String lecture_subset){
		char[] chars = lecture_subset.toCharArray();
		Arrays.sort(chars);
		String sorted = new String(chars);
		if(m_dynamic_scores.containsKey(sorted))
			return m_dynamic_scores.get(sorted);
		else
			return -1.0;
	}
	
	public void UpdateBestScores(Double score){
		Double min_score = m_best_combinatorial_score.peek();
		if(score > min_score){
			if(m_best_combinatorial_score.size() == 10){
				m_best_combinatorial_score.poll();
			}
			m_best_combinatorial_score.add(score);
		}
	}
	
	
	
	public static void main(String[] args){
		//String arr = "112233";
		//int[] lecture_list = {1,1,2,2,3,3};
		SpacedOut spaced_out = new SpacedOut();
		//spaced_out.CheckPassbyValue(lecture_list);
		HashMap<String,Integer> subject_count = new HashMap<String, Integer>();
		subject_count.put("Maths", 3);
		subject_count.put("English", 2);
		subject_count.put("SST", 2);
		subject_count.put("Computer Science", 1);
//		subject_count.put("Physics", 1);
//		subject_count.put("Chemistry", 1);
//		subject_count.put("Sub1", 1);
//		subject_count.put("Sub2", 1);
//		subject_count.put("Sub3", 1);
//		subject_count.put("Sub4", 1);
		spaced_out.SingleClassTimeTables(subject_count);
	}

/*	private void CheckPassbyValue(char[] lecture_list) {
		System.out.println(lecture_list);
		char[] new_lecture_list = lecture_list.clone();
		CharSwap(new_lecture_list, 0, 2);
		System.out.println(lecture_list);
	}*/

	private void SingleClassTimeTables(HashMap<String, Integer> subject_count_map) {
		ArrayList<Integer> lecture_list = CreateLectureList(subject_count_map);
		Permute(lecture_list, 0);
		PrintRankedTimeTables(m_ranked_timetable);
	}
	
	private ArrayList<Integer> CreateLectureList(HashMap<String, Integer> subject_count_map) {
		ArrayList<Integer> lecture_list = new ArrayList<Integer>();
		int i = 0;
		for (Map.Entry<String, Integer> entry : subject_count_map.entrySet()) {
			m_subject_code.put(i, entry.getKey());
			int subject_count = entry.getValue();
			for (int j = 0; j < subject_count; j++) {
				lecture_list.add(i);
			}
			i++;
		}
		return lecture_list;
	}
	public Double Permute(ArrayList<Integer> lecture_list, int i){
		String permute_memory_member = lecture_list.toString();
		String sub_lecture_list = permute_memory_member.substring(i, permute_memory_member.length()-1);
		Double sub_lecture_dynamic_score = DynamicScore(sub_lecture_list);
		Double min_top_score = MinScoreInBestScores();
		
		if(m_permute_memory.contains(permute_memory_member))
			return sub_lecture_dynamic_score;
		
		else if(i==lecture_list.size()){
			ComputeDistance(lecture_list);
			return 0.0;
		}
		
		else if(sub_lecture_dynamic_score != -1 || sub_lecture_dynamic_score < min_top_score){
			return sub_lecture_dynamic_score;
		}
		
		Double max = 0.0;
		for (int j = i; j < lecture_list.size(); j++) {
			ArrayList<Integer> new_lecture_list = new ArrayList<Integer>(lecture_list);
			new_lecture_list = LectureSwap(new_lecture_list, i, j);
			Double value = Permute(new_lecture_list, i + 1) + SpaceCurrent(new_lecture_list, i);
			if(value > max)
				max = value;
			String new_permute_memory_member = new_lecture_list.toString() + Integer.toString(i);
			m_permute_memory.add(new_permute_memory_member);
		}
		UpdateDynamicScore(sub_lecture_list,max);
		UpdateBestScores(max);
		return max;
	}

	private void UpdateDynamicScore(String sub_lecture_list, Double max) {
		char[] chars = sub_lecture_list.toCharArray();
		Arrays.sort(chars);
		String sorted = new String(chars);
		m_dynamic_scores.put(sorted, max);
	}

	private Double SpaceCurrent(ArrayList<Integer> new_lecture_list, int i) {
		return null;
	}

	private ArrayList<Integer> LectureSwap(ArrayList<Integer> new_lecture_list, int i, int j) {
		Collections.swap(new_lecture_list, i, j);
		return new_lecture_list;
	}

	private void ComputeDistance(ArrayList<Integer> lecture_list) {
		HashMap<Integer, Integer> last_element_index = new HashMap<Integer, Integer>();
		double score = 0;
		for (int i = 0; i < lecture_list.size(); i++) {
			Integer lecture_key = lecture_list.get(i);
			if(last_element_index.containsKey(lecture_key)){
				int last_position = last_element_index.get(lecture_key).intValue();
				score = score + Math.sqrt(i-last_position);
				last_element_index.put(lecture_key, i);
			}
			else
				last_element_index.put(lecture_key, i);
		}
		String solution_lecture_list = lecture_list.toString();
		//System.out.println(solution_lecture_list + " : " + Double.toString(score));
		m_ranked_timetable.put(solution_lecture_list, score);
		//System.out.println(solution_lecture_list + " : " + score);
		//PrintRankedTimeTables(ranked_timetable);
	}
	
	private void PrintRankedTimeTables(HashMap<String, Double> ranked_timetable) {
		HashMap<String, Double> sorted_ranked_timetable = (HashMap<String, Double>) SortMapByValue(ranked_timetable);
		//System.out.println(sorted_ranked_timetable.size());
		//System.out.println(sorted_ranked_timetable);
		int count = 1;
		for(java.util.Map.Entry<String, Double> entry : sorted_ranked_timetable.entrySet()){
			System.out.println("[" + count + "] " + entry.getKey() + " : " + entry.getValue());
			count++;
		}
	}
	
	public static <K, V extends Comparable<? super V>> Map<K, V> 
	SortMapByValue( Map<K, V> map )
	{
	    List<Map.Entry<K, V>> list =
	        new LinkedList<>( map.entrySet() );
	    Collections.sort( list, new Comparator<Map.Entry<K, V>>()
	    {
	        @Override
	        public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
	        {
	            return (o1.getValue()).compareTo( o2.getValue() );
	        }
	    } );
	
	    Map<K, V> result = new LinkedHashMap<>();
	    for (Map.Entry<K, V> entry : list)
	    {
	        result.put( entry.getKey(), entry.getValue() );
	    }
	    return result;
	}
	
}
