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
	public class CombinatorialScore {
		Double m_raw_combinatorial_score;
		ArrayList<Integer> m_best_sub_permutation;
		public Double RawCombinatorialScore() {
			// TODO Auto-generated method stub
			return null;
		}
		public ArrayList<Integer> BestSubPermutation() {
			// TODO Auto-generated method stub
			return null;
		}
		public void SetRawCombinatorialScore(Double raw_combinatorial_score) {
			// TODO Auto-generated method stub
			
		}
		public void SetBestSubPermuatation(
				ArrayList<Integer> best_sub_permutation) {
			// TODO Auto-generated method stub
			
		}
	}

	private HashSet<String> m_permute_memory;
	private HashMap<String, Double> m_ranked_timetable;
	private Map<Integer, String> m_subject_code;
	
	private Queue<Double> m_top_k_scores = new PriorityQueue<>(10); 
	private Map<String, CombinatorialScore> m_best_combinatorial_scores;
	public int m_permuation_count;
	
	public SpacedOut(){
		m_permute_memory = new HashSet<String>();
		m_ranked_timetable = new HashMap<String, Double>();
		m_subject_code = new HashMap<Integer, String>();
		m_ranked_timetable.clear();
		m_top_k_scores.clear();
		m_best_combinatorial_scores = new HashMap<String, Double>();
		m_permuation_count = 0;
	}
	
	public Double MinInTopKScores(){
		return m_top_k_scores.peek();
	}
	
	public CombinatorialScore BestCombinatorialScore(ArrayList<Integer> lecture_list, int i){
		ArrayList<Integer> sub_node_structure = new ArrayList<>();
		sub_node_structure.subList(i, lecture_list.size()-1);
		String sub_node_structure_hashed = sub_node_structure.toString();
		char[] chars = sub_node_structure_hashed.toCharArray();
		Arrays.sort(chars);
		String sub_node_structure_sorted = new String(chars);
		return m_best_combinatorial_scores.get(sub_node_structure_sorted);
	}
	
	public void UpdateTopKScores(Double score){
		Double min_score = m_top_k_scores.peek();
		if(score > min_score){
			if(m_top_k_scores.size() == 10){
				m_top_k_scores.poll();
			}
			m_top_k_scores.add(score);
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
		subject_count.put("Physics", 1);
		subject_count.put("Chemistry", 1);
		subject_count.put("Sub1", 1);
		subject_count.put("Sub2", 1);
		subject_count.put("Sub3", 1);
		subject_count.put("Sub4", 1);
		spaced_out.SingleClassTimeTables(subject_count);
		System.out.println(spaced_out.m_permuation_count);
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
	public CombinatorialScore Permute(ArrayList<Integer> lecture_list, int i){
		if(IsBestCombinatorialScore(lecture_list, i)){
			CombinatorialScore combinatorial_score = BestCombinatorialScore(lecture_list, i);
			Double node_best_combinatorial_score = NodeBestCombinatorialScore(lecture_list, i, combinatorial_score);
			Double min_top_k_score = MinInTopKScores();
			if(node_best_combinatorial_score <= min_top_k_score){
				return combinatorial_score;
			}
			else{
				UpdateTopKScores(node_best_combinatorial_score);
			}
		}
		
		else if(IsPermuteNodeStructureTraversed(lecture_list, i))
			return BestCombinatorialScore(lecture_list, i);
		
		else if(i==lecture_list.size()){
			m_permuation_count++;
			Spacing(lecture_list);
			return DummyCombinatorialScore();
		}
		
		
		Double max = 0.0;
		CombinatorialScore max_combinatorial_score = new CombinatorialScore();
		for (int j = i; j < lecture_list.size(); j++) {
			ArrayList<Integer> new_lecture_list = new ArrayList<Integer>(lecture_list);
			int first_value = new_lecture_list.get(i).intValue();
			int second_value = new_lecture_list.get(j).intValue();
			if( first_value != second_value || i == j){
				new_lecture_list = LectureSwap(new_lecture_list, i, j);
				CombinatorialScore combinatorial_score = Permute(new_lecture_list, i + 1); // + SpaceCurrent(new_lecture_list, i);
				ArrayList<Integer> left = new ArrayList<>();
				left.add(lecture_list.get(i));
				ArrayList<Integer> right = new ArrayList<>(lecture_list.subList(i+1, lecture_list.size()-1));
				Double raw_combinatorial_score = combinatorial_score.RawCombinatorialScore() 
						+ SpacingBetween(left, right);
				if(raw_combinatorial_score > max){
					max = raw_combinatorial_score;
					ArrayList<Integer> best_branched_sub_permutation = combinatorial_score.BestSubPermutation();
					ArrayList<Integer> best_sub_permutation = new ArrayList<>();
					best_sub_permutation.add(lecture_list.get(i).intValue());
					best_sub_permutation.addAll(best_branched_sub_permutation);
					max_combinatorial_score.SetRawCombinatorialScore(raw_combinatorial_score);
					max_combinatorial_score.SetBestSubPermuatation(best_sub_permutation);
				}
				String new_permute_memory_member = new_lecture_list.toString() + Integer.toString(i);
				m_permute_memory.add(new_permute_memory_member);
			}
		}
		return max_combinatorial_score;
	}

	private boolean IsPermuteNodeStructureTraversed(
			ArrayList<Integer> lecture_list, int i) {
		// TODO Auto-generated method stub
		return false;
	}

	private CombinatorialScore DummyCombinatorialScore() {
		return null;
	}

	private Double NodeBestCombinatorialScore(ArrayList<Integer> lecture_list,
			int i, CombinatorialScore combinatorial_score) {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean IsBestCombinatorialScore(ArrayList<Integer> lecture_list,
			int i) {
		ArrayList<Integer> sub_node_structure = new ArrayList<>();
		sub_node_structure.subList(i, lecture_list.size()-1);
		String sub_node_structure_hashed = sub_node_structure.toString();
		char[] chars = sub_node_structure_hashed.toCharArray();
		Arrays.sort(chars);
		String sub_node_structure_sorted = new String(chars);
		return m_best_combinatorial_scores.containsKey(sub_node_structure_sorted);
	}

	private void UpdateBestCombinatorialScore(String sub_lecture_list, CombinatorialScore max) {
		char[] chars = sub_lecture_list.toCharArray();
		Arrays.sort(chars);
		String sorted = new String(chars);
		m_best_combinatorial_scores.put(sorted, max);
	}

	private Double SpacingBetween(ArrayList<Integer> first_lecture_set, ArrayList<Integer> second_lecture_set) {
		return null;
	}

	private ArrayList<Integer> LectureSwap(ArrayList<Integer> new_lecture_list, int i, int j) {
		Collections.swap(new_lecture_list, i, j);
		return new_lecture_list;
	}

	private Double Spacing(ArrayList<Integer> lecture_list) {
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
		return score;
	}
	
	private void UpdateRankedTimeTables(ArrayList<Integer> lecture_list, Double score){
		String solution_lecture_list = lecture_list.toString();
		m_ranked_timetable.put(solution_lecture_list, score);		
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
