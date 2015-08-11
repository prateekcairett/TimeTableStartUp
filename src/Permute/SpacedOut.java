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
			return m_raw_combinatorial_score;
		}
		public ArrayList<Integer> BestSubPermutation() {
			return m_best_sub_permutation;
		}
		public void SetRawCombinatorialScore(Double raw_combinatorial_score) {
			m_raw_combinatorial_score = raw_combinatorial_score;
		}
		public void SetBestSubPermuatation(
				ArrayList<Integer> best_sub_permutation) {
			m_best_sub_permutation = best_sub_permutation;
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
		InitialiseMinkTopScores();
		m_best_combinatorial_scores = new HashMap<String, CombinatorialScore>();
		m_permuation_count = 0;
	}
	
	private void InitialiseMinkTopScores() {
		for (int i = 0; i < 10; i++) {
			m_top_k_scores.add(-1.0);
		}
	}

	public Double MinInTopKScores(){
		return m_top_k_scores.peek();
	}
	
	public CombinatorialScore BestCombinatorialScore(ArrayList<Integer> lecture_list, int i){
		ArrayList<Integer> sub_node_structure = SubList(lecture_list, i, lecture_list.size()-1);
		String sub_node_structure_hashed = ToString(sub_node_structure);
		char[] chars = sub_node_structure_hashed.toCharArray();
		Arrays.sort(chars);
		String sub_node_structure_sorted = new String(chars);
		CombinatorialScore combinatorial_score = m_best_combinatorial_scores.get(sub_node_structure_sorted);
		System.out.println("Sorted from BestCombinatoriaScore : " + sub_node_structure_sorted);
		return combinatorial_score;
	}
	
	public void UpdateTopKScores(Double score){
		Double min_score = m_top_k_scores.peek();
		if(score > min_score){
			if(m_top_k_scores.size() == 100000){
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
			Double score = Spacing(lecture_list);
			UpdateRankedTimeTables(lecture_list, score);
			return BaseCombinatorialScore();
		}
		
		
		Double max = -500.0;
		CombinatorialScore max_combinatorial_score = new CombinatorialScore();
		ArrayList<Integer> right = SubList(lecture_list, i, lecture_list.size()-1);
		for (int j = i; j < lecture_list.size(); j++) {
			ArrayList<Integer> new_lecture_list = new ArrayList<Integer>(lecture_list);
			int first_value = new_lecture_list.get(i).intValue();
			int second_value = new_lecture_list.get(j).intValue();
			if( first_value != second_value || i == j){
				new_lecture_list = LectureSwap(new_lecture_list, i, j);
				CombinatorialScore combinatorial_score = Permute(new_lecture_list, i + 1); // + SpaceCurrent(new_lecture_list, i);
				int left = lecture_list.get(i).intValue();
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
		String combination = SortedLectureList(right);
		UpdateBestCombinatorialScore(combination, max_combinatorial_score);
		System.out.println(lecture_list.toString() + " : " + i + " : " + combination + " : " + max_combinatorial_score.RawCombinatorialScore());
		return max_combinatorial_score;
	}

	private String SortedLectureList(ArrayList<Integer> right) {
		String combination = ToString(right);
		char[] chars = combination.toCharArray();
		Arrays.sort(chars);
		String sorted_combination = new String(chars); 
		return sorted_combination;
	}

	private boolean IsPermuteNodeStructureTraversed(
			ArrayList<Integer> lecture_list, int i) {
		String permute_memory_member = lecture_list.toString() + Integer.toString(i);
		return m_permute_memory.contains(permute_memory_member);
	}

	private CombinatorialScore BaseCombinatorialScore() {
		CombinatorialScore combinatorial_score = new CombinatorialScore();
		ArrayList<Integer> base_lecture_list = new ArrayList<Integer>();
		//base_lecture_list.add(-1);
		combinatorial_score.SetBestSubPermuatation(base_lecture_list);
		combinatorial_score.SetRawCombinatorialScore(-0.0);
		return combinatorial_score;
	}

	private Double NodeBestCombinatorialScore(ArrayList<Integer> lecture_list,
			int i, CombinatorialScore combinatorial_score) {
		ArrayList<Integer> static_sub_lecture_list = SubLectureList(lecture_list, 0, i-1);
		ArrayList<Integer> best_sub_lecture_permutation = combinatorial_score.m_best_sub_permutation;
		ArrayList<Integer> combined_sub_lectures = CombineSubLectures(static_sub_lecture_list, best_sub_lecture_permutation);
		Double score = Spacing(combined_sub_lectures);
		return score;
	}


	private ArrayList<Integer> CombineSubLectures(
			ArrayList<Integer> static_sub_lecture_list,
			ArrayList<Integer> best_sub_lecture_permutation) {
		ArrayList<Integer> combined_sub_lectures = new ArrayList<Integer>();
		combined_sub_lectures.addAll(static_sub_lecture_list);
		combined_sub_lectures.addAll(best_sub_lecture_permutation);
		return combined_sub_lectures;
	}

	private ArrayList<Integer> SubLectureList(ArrayList<Integer> lecture_list,
			int i, int j) {
		ArrayList<Integer> sub_lectures = new ArrayList<Integer>(lecture_list.subList(i, j));
		return sub_lectures;
	}
	
	private ArrayList<Integer> SubList(ArrayList<Integer> source_array, int from_index, int to_index){
		ArrayList<Integer> sub_list = new ArrayList<Integer>();
		for (int i = from_index; i <= to_index; i++) 
			sub_list.add(source_array.get(i).intValue());
		return sub_list;
	}
	
	private String ToString(ArrayList<Integer> source_array){
		String string_array = new String();
		for (int i = 0; i < source_array.size(); i++) {
			string_array = string_array.concat(source_array.get(i).toString());
		}
		return string_array;
	}

	private boolean IsBestCombinatorialScore(ArrayList<Integer> lecture_list,
			int i) {
		ArrayList<Integer> sub_node_structure = new ArrayList<Integer>();
		sub_node_structure = SubList(lecture_list, i, lecture_list.size()-1);
		String sub_node_structure_hashed = ToString(sub_node_structure);
		char[] chars = sub_node_structure_hashed.toCharArray();
		Arrays.sort(chars);
		String sub_node_structure_sorted = new String(chars);
		boolean result = m_best_combinatorial_scores.containsKey(sub_node_structure_sorted);
		if(result == true)
			System.out.println("Sorted " + sub_node_structure_sorted + " found");
		return result;
	}

	private void UpdateBestCombinatorialScore(String sub_lecture_list, CombinatorialScore max) {
		char[] chars = sub_lecture_list.toCharArray();
		Arrays.sort(chars);
		String sorted = new String(chars);
		m_best_combinatorial_scores.put(sorted, max);
		System.out.println("Updated best combinatorial score for " + sorted + " as " + max.RawCombinatorialScore());
	}

	private Double SpacingBetween(int first_lecture, ArrayList<Integer> second_lecture_set) {
		Double space_between = 0.0;
		for (int i = 0; i < second_lecture_set.size(); i++) {
			int value = second_lecture_set.get(i).intValue();
			if(value == first_lecture){
				space_between = (double) (i+1);
			}		
		}
		return space_between;
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
