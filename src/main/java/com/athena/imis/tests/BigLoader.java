package com.athena.imis.tests;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.TIntHashSet;
import gnu.trove.set.hash.TLongHashSet;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.jena.graph.Triple;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.lang.PipedQuadsStream;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.riot.lang.PipedTriplesStream;
import org.apache.jena.sparql.core.Quad;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Fun;
import org.mapdb.Serializer;

import com.athena.imis.models.BigCharacteristicSet;
import com.athena.imis.models.BigExtendedCharacteristicSet;

/**
 * This class is responsible for loading RDF files in blinkDB. It has been tested with NT and RDF/XML serializations.
 * Use this class as is in order to load a file into blinkDB.
 * Two parameters are required, args[0] contains the file for the database you want to create, args[1] the 
 * name to give the new database (as a mapDB instance) and args[2] the path to the RDF file.
 * 
 * @author Marios Meimaris
 *
 */
public class BigLoader {
	
		
		public static Map<String, Integer> propertiesSet = new THashMap<String, Integer>();
		public static Map<Integer, String> revPropertiesSet = new THashMap<Integer, String>();
		public static Map<String, Integer> intMap = new THashMap<String, Integer>();
		public static Map<String, Integer> intMapDB ; 
		//public static Map<Integer, String> reverseIntMap ;//= new THashMap<Integer, String>();
		
		public static void main(String[] args) {
			
			
		
			//Dataset ds = TDBFactory.createDataset(args[0]);	   
			
			//Model model = ds.getDefaultModel();
			
			DB db = DBMaker.newFileDB(new File(args[0]))
					.transactionDisable()
	 				.fileChannelEnable() 			
	 				.fileMmapEnable()
	 				.asyncWriteFlushDelay(5000)
	 				.cacheSize(1000000000) 				
	 				.closeOnJvmShutdown()
	 				.make();
			
			/*intMapDB = db.hashMapCreate("intMap")
	 				.keySerializer(Serializer.STRING)
	 				.valueSerializer(Serializer.INTEGER)
	 				.makeOrGet();*/
			
			 
			/*reverseIntMap = db.hashMapCreate("reverseIntMap")
	 				.keySerializer(Serializer.INTEGER)
	 				.valueSerializer(Serializer.STRING)
	 				.makeOrGet();*/
			/*Map<String, Integer> prefixMap = new HashMap<String, Integer>();
			Map<String, Integer> prefixMapDB = db.hashMapCreate("prefixMap")
	 				.keySerializer(Serializer.STRING)
	 				.valueSerializer(Serializer.INTEGER)
	 				.makeOrGet();*/
			
			Map<Integer, Integer> dbECSMap = db.hashMapCreate(args[1])
	 				.keySerializer(Serializer.INTEGER)
	 				.valueSerializer(Serializer.INTEGER)
	 				.makeOrGet();
			dbECSMap.clear();
			int next = 0;
			//LLongArray l = LArrayJ.newLLongArray(model.size());
								
			//final int[][] array ;//= new int[(int)model.size()][4];
			final ArrayList<int[]> array = new ArrayList<int[]>();
			
			/*for(int i = 0; i < array.size(); i++){
				int[] ar = new int[4];
				ar[3] = -1;
				array.add(i, ar);
			}*/
			int propIndex = 0, nextInd = 0;
			long start = System.nanoTime();
			PipedRDFIterator<Triple> iter = new PipedRDFIterator<Triple>(1024*1440*10);
			//PipedRDFIterator<Quad> iter = new PipedRDFIterator<Quad>(1024*1440*10);
		    final PipedRDFStream<Triple> inputStream = new PipedTriplesStream(iter);
			//final PipedRDFStream<Quad> inputStream = new PipedQuadsStream(iter);

		        // PipedRDFStream and PipedRDFIterator need to be on different threads
		    ExecutorService executor = Executors.newSingleThreadExecutor();

		        // Create a runnable for our parser thread
		    Runnable parser = new Runnable() {

		            @Override
		            public void run() {
		                // Call the parsing process.
		                RDFDataMgr.parse(inputStream, args[2]);
		            	//RDFDataMgr.parse(inputStream, "C:/temp/tdb_export.rdf");
		            }
		     };

		        // Start the parser on another thread
		    executor.submit(parser);

		        // We will consume the input on the main thread here

		        // We can now iterate over data as it is parsed, parsing only runs as
		        // far ahead of our consumption as the buffer size allows
		    Triple triple ;
		    //Quad triple;
		    String s, p, o;
		    
		    //Map<String, Integer> prefixMap = new HashMap<String, Integer>();
		    
		    int prefixIndex = 0, lastIndex ;
		    
		    String prefix = "";
		    
		    int triplesParsed = 0;
		    
		    while (iter.hasNext()) {
		    
		    	triple = iter.next();
		        triplesParsed++;		        
		    	s = triple.getSubject().toString();
		        
		    	//uncomment for more prefix compression
		    	/*if(s.lastIndexOf('#') >= 0){
		            	lastIndex = s.lastIndexOf('#');
		            	prefix = s.substring(0, lastIndex+1);
		            	if(!prefixMap.containsKey(prefix))
		            		prefixMap.put(prefix, prefixIndex++);
		            }
		            else{
		            	lastIndex = s.lastIndexOf('/');
		            	prefix = s.substring(0, lastIndex+1);
		            	if(!prefixMap.containsKey(prefix))
		            		prefixMap.put(prefix, prefixIndex++);
		            }
		            s = "_"+prefixMap.get(prefix)+":"+s.substring(lastIndex+1);*/
		            //System.out.println(s);
		        
		    	p = triple.getPredicate().toString();
		        
		    	o = triple.getObject().toString();
		            /*if(triple.getObject().isURI()){
		            	if(o.lastIndexOf('#') >= 0){
			            	lastIndex = o.lastIndexOf('#');
			            	prefix = o.substring(0, lastIndex+1);
			            	if(!prefixMap.containsKey(prefix))
			            		prefixMap.put(prefix, prefixIndex++);
			            }
			            else{
			            	lastIndex = o.lastIndexOf('/');
			            	prefix = o.substring(0, lastIndex+1);
			            	if(!prefixMap.containsKey(prefix))
			            		prefixMap.put(prefix, prefixIndex++);
			            }
			            o = "_"+prefixMap.get(prefix)+":"+o.substring(lastIndex+1);
		            }*/
		        
		    	if(!propertiesSet.containsKey(p)){							
			    
		    		propertiesSet.put(p, propIndex++);	    	
			    	
		    	}
				
		    	if(!intMap.containsKey(s)){		    				    
		    	
		    		intMap.put(s, nextInd++);
		    		
		    	}
				
		    	if(!intMap.containsKey(o)){		   			   
		    	
		    		if(triple.getObject().isURI())
		    			intMap.put(o, nextInd++);
		    		else
		    			intMap.put(o, Integer.MAX_VALUE);
		    		
		    	}
		            
		    	int[] ar = new int[4];
				ar[0] = intMap.get(s);//spLong;
				ar[1] = propertiesSet.get(p);//spLong;
				ar[2] = intMap.get(o);//spLong;			
				ar[3] = -1;
				array.add(next, ar);
				next++;
		    }
		    executor.shutdown();
		    iter.close();
		    long end = System.nanoTime();
			System.out.println("triples parsed:" + triplesParsed);
			System.out.println("piped: " + (end-start));
			List<String> keys = new ArrayList<String>(intMap.keySet());
			Collections.sort(keys);
			Collections.reverse(keys);
			//keys.addAll(intMap.keySet());
			Iterator<String> source = keys.iterator();
			Fun.Function1<Integer,String> valueExtractor = new Fun.Function1<Integer, String>() {
		            @Override public Integer run(String s) {
		                return intMap.get(s);
		            }
		        };
		    intMapDB = db.createTreeMap("intMap")
		                .pumpSource(source,valueExtractor)
		                //.pumpPresort(100000) // for presorting data we could also use this method
		                .keySerializer(Serializer.STRING)
		                .valueSerializer(Serializer.INTEGER)
		                .make();
			for(String st : intMap.keySet()){
				intMapDB.put(st, intMap.get(st));
			}
			for(String st : intMap.keySet()){
				intMapDB.put(st, intMap.get(st));
			}
			/*for(String pr : prefixMap.keySet()){
				prefixMapDB.put(pr, prefixMap.get(pr));
			}*/
		 
			
			System.out.println("loading: " + (end-start));
			System.out.println("size: " + array.size());
			start = System.nanoTime();
			//Arrays.sort(array);
			Collections.sort(array, new Comparator<int[]>() {
			    public int compare(int[] s1, int[] s2) {
			        if (s1[0] > s2[0])
			            return 1;    // tells Arrays.sort() that s1 comes after s2
			        else if (s1[0] < s2[0])
			            return -1;   // tells Arrays.sort() that s1 comes before s2
			        else {			          
			            return 0;
			        }
			    }
			});
			end = System.nanoTime();
			System.out.println("sorting: " + (end-start));
			
			int previousSubject = Integer.MIN_VALUE;
			
			TIntHashSet properties = new TIntHashSet();
			
			HashMap<BigCharacteristicSet, Integer> ucs = new HashMap<>();
		
			Map<Integer, BigCharacteristicSet> rucs = db.hashMapCreate("rucsMap")
	 				.keySerializer(Serializer.INTEGER)
	 				//.valueSerializer(Serializer.)
	 				.makeOrGet();
			start = System.nanoTime();
			int neg = 0;
			int csIndex = 0;
			/*for(int i = 0; i < l.size(); i++){
				long t = l.apply((long)i);*/
			int previousStart = 0;
			BigCharacteristicSet cs = null;
			int[] t ;
			int subject ;
			int prop ;
			for(int i = 0; i < array.size(); i++){
				t = array.get(i);
				subject = t[0];
				prop = t[1];
				
				if(i > 0 && previousSubject != subject){
										
					cs = new BigCharacteristicSet(properties, true);					
					if(!ucs.containsKey(cs)){
						
						dbECSMap.put(previousSubject, csIndex);
						rucs.put(csIndex, cs);
						for(int j = previousStart; j < i; j++)
							array.get(j)[3] = csIndex;
						ucs.put(cs, csIndex++);
						
						
					}
					else{
						dbECSMap.put(previousSubject, ucs.get(cs));
						//array[i-1][3] = ucs.get(cs);
						for(int j = previousStart; j < i; j++)
							array.get(j)[3] = ucs.get(cs);
					}
					previousStart = i;
					properties.clear();
				}
				if(!properties.contains(prop))
					properties.add(prop);
				previousSubject = subject;
			}
			/*for(Integer subject : sp.keySet()){
				HashSet<Integer> props = sp.get(subject);
				BigCharacteristicSet cs = new BigCharacteristicSet(props, true);
				ucs.add(cs);
			}*/
			
			if(!properties.isEmpty()){
				cs = new BigCharacteristicSet(properties, true);
				if(!ucs.containsKey(cs)){
					//array[array.length-1][3] = csIndex; 
					for(int j = previousStart; j < array.size(); j++)
						array.get(j)[3] = csIndex;
					dbECSMap.put(previousSubject, csIndex);
					rucs.put(csIndex, cs);
					ucs.put(cs, csIndex);
					
				}
				else{
					for(int j = previousStart; j < array.size(); j++)
						array.get(j)[3] = ucs.get(cs);
					//array[array.length-1][3] = ucs.get(cs);
					dbECSMap.put(previousSubject, ucs.get(cs));
				}
				
			}
			end = System.nanoTime();
			System.out.println("ucs time: " + (end-start));
			start = System.nanoTime();
			Collections.sort(array, new Comparator<int[]>() {
			    public int compare(int[] s1, int[] s2) {
			        if (s1[3] > s2[3])
			            return 1;    // s1 comes after s2
			        else if (s1[3] < s2[3])
			            return -1;   // s1 comes before s2
			        else {			          
			            return 0;
			        }
			    }
			});
			ArrayList<Long> tripleList = new ArrayList<Long>();
			Map<Integer, long[]> csMap = db.hashMapCreate("csMap")
	 				.keySerializer(Serializer.INTEGER)
	 				.valueSerializer(Serializer.LONG_ARRAY)
	 				.makeOrGet();
			csMap.clear();
			csIndex = array.get(0)[3];
			neg = 0;
			
			for(int i = 0; i < array.size(); i++){
				
				t = array.get(i);
				
				if(csIndex != t[3]){
					//System.out.println(csIndex);
					long[] result = tripleList.stream().mapToLong(k -> k).toArray();
					//Arrays.sort(result);
					csMap.put(csIndex, result);					
					tripleList = new ArrayList<Long>();
				}
				csIndex = t[3];
				long lon = ((long)t[1] << 54 | 
						(long)(t[0] & 0x7FFFFFF) << 27 | 
						(long)((t[2] & 0x7FFFFFF)));
				
				tripleList.add(lon);	
				
				
			}		

			long[] result = tripleList.stream().mapToLong(k -> k).toArray();
			//Arrays.sort(result);
			csMap.put(csIndex, result);
			
			end = System.nanoTime();
			System.out.println("ucs2 time: " + (end-start));
			System.out.println("csMap size: " + csMap.size());
			int tot = 0;


			Map<BigExtendedCharacteristicSet, long[]> ecsMap = new THashMap<BigExtendedCharacteristicSet, long[]>();
			
			start = System.nanoTime();
			//HashMap<Integer, ArrayList<Long>> hash = new HashMap<Integer, ArrayList<Long>>();
			Map<Integer, ArrayList<Long>> hash = new THashMap<>();
			List<Long> resList = null ;
			ArrayList<Long> def = null;
			BigExtendedCharacteristicSet ecs = null;
			
			Map<Integer, long[]> ecsLongArrayMap = db.hashMapCreate("ecsLongArrays")
	 				.keySerializer(Serializer.INTEGER)
	 				.valueSerializer(Serializer.LONG_ARRAY)
	 				.makeOrGet();
			
			Map<Integer, BigExtendedCharacteristicSet> ruecs = db.hashMapCreate("ruecsMap")
	 				.keySerializer(Serializer.INTEGER)	 			
	 				.makeOrGet();
			
			Map<BigExtendedCharacteristicSet, Integer> uecs = db.hashMapCreate("uecsMap")
	 				.valueSerializer(Serializer.INTEGER)	 				
	 				.makeOrGet();
			
			int ecsIndex = 0;
			HashMap<BigCharacteristicSet, HashSet<BigExtendedCharacteristicSet>> sCSToECS = new HashMap<BigCharacteristicSet, HashSet<BigExtendedCharacteristicSet>>();
			HashMap<BigCharacteristicSet, HashSet<BigExtendedCharacteristicSet>> oCSToECS = new HashMap<BigCharacteristicSet, HashSet<BigExtendedCharacteristicSet>>();
			HashSet<BigExtendedCharacteristicSet> d;
			for(Integer cs1 : csMap.keySet()){
				
				hash.clear();
				
				for(long lon : csMap.get(cs1)){
					def = hash.getOrDefault((int)(lon & 0x7FFFFFF), new ArrayList<Long>());
					def.add(lon);
					hash.put((int)(lon & 0x7FFFFFF), def);	
					
				}
				
				for(Integer cs2 : csMap.keySet()){
					
					//if(vis.contains(cs2)) continue;
					resList = join(hash, csMap.get(cs2));
					if(resList.isEmpty()) continue;
					
					ecs = new BigExtendedCharacteristicSet(rucs.get(cs1), rucs.get(cs2));
					d = sCSToECS.getOrDefault(rucs.get(cs1), new HashSet<BigExtendedCharacteristicSet>());
					d.add(ecs);
					sCSToECS.put(rucs.get(cs1), d);
					d = oCSToECS.getOrDefault(rucs.get(cs2), new HashSet<BigExtendedCharacteristicSet>());
					d.add(ecs);
					oCSToECS.put(rucs.get(cs2), d);
					result = resList.stream().mapToLong(k -> k).toArray();
					Arrays.sort(result);
					//ecsMap.put(ecs, result);
					ecsLongArrayMap.put(ecsIndex, result);
					uecs.put(ecs, ecsIndex);
					ruecs.put(ecsIndex++, ecs);
					
				
				}
				
				resList.clear();
				for(long lon : csMap.get(cs1)){
					if(!filter.contains(lon)){
						resList.add(lon);
						
					}
				}
				if(!resList.isEmpty()){
					ecs = new BigExtendedCharacteristicSet(rucs.get(cs1), null);
					result = resList.stream().mapToLong(k -> k).toArray();
					//ecsMap.put(ecs, result);
					Arrays.sort(result);
					ecsLongArrayMap.put(ecsIndex, result);
					uecs.put(ecs, ecsIndex);
					ruecs.put(ecsIndex++, ecs);
					d = sCSToECS.getOrDefault(rucs.get(cs1), new HashSet<BigExtendedCharacteristicSet>());
					d.add(ecs);
					sCSToECS.put(rucs.get(cs1), d);			
					//tot += result.length;
				}
				filter.clear();
				//vis.add(cs1);
				//visitedECS.add(cs1);
			}
			end = System.nanoTime();
			System.out.println("ecs new: " + (end-start));
			
			System.out.println("ecsMap: " + ecsLongArrayMap.size());
			System.out.println("orphans: " + tot);
			System.out.println("filter: " + filter.size());
			Map<BigExtendedCharacteristicSet, HashSet<BigExtendedCharacteristicSet>> ecsLinks = db.hashMapCreate("ecsLinks")
	 				//.keySerializer(Serializer.INTEGER)	 			
	 				.makeOrGet(); 
			for(BigCharacteristicSet cs1 : oCSToECS.keySet()){
 	 			if(sCSToECS.containsKey(cs1)){
 	 				for(BigExtendedCharacteristicSet e1 : oCSToECS.get(cs1)){
 	 					for(BigExtendedCharacteristicSet e2 : sCSToECS.get(cs1)){
 	 						d = ecsLinks.getOrDefault(e1, new HashSet<BigExtendedCharacteristicSet>());
 	 						d.add(e2);
 	 						ecsLinks.put(e1, d); 						
 	 					}
 	 				}
 	 				
 	 			}
 	 		}
			tot = 0;
			for(BigExtendedCharacteristicSet e : ecsLinks.keySet()){
				tot += ecsLinks.get(e).size();
			}
			System.out.println("total ecs links: " + tot);
			tot = 0;
			for(Integer ecs1 : ecsLongArrayMap.keySet()){
				tot += ecsLongArrayMap.get(ecs1).length;
			}
			System.out.println("tot " + tot);
			//System.out.println("notot " + notot);
			
			System.out.println("p " + propIndex);
			//System.out.println("s " + neg);
			System.out.println("UCS: " + ucs.size());
			start = System.nanoTime();
			Map<Integer, int[]> propIndexMap = db.hashMapCreate("propIndexMap")
	 				.keySerializer(Serializer.INTEGER)	
	 				.valueSerializer(Serializer.INT_ARRAY)
	 				.makeOrGet();  
					//new HashMap<Integer, HashMap<Integer,Integer>>();
			for(Integer e : ecsLongArrayMap.keySet()){
					//propIndexMap.put(e, new HashMap<Integer, Integer>());					
					long[] larr = ecsLongArrayMap.get(e);
					int[] parr = new int[propertiesSet.size()];
	 			for(String property : propertiesSet.keySet()){
	 				int ps = propertiesSet.get(property);
	 				int pstart = indexOfProperty(larr, ps);
	 				if(pstart < 0) {
	 					parr[ps] = -1;
	 					continue;
	 				}
	 				parr[ps] = pstart;
	 				//propIndexMap.get(e).put(ps, pstart);
	 			}
	 			propIndexMap.put(e, parr);
			}
			
			Map<String, Integer> propertiesSetBack = db.hashMapCreate("propertiesSet")
	 				.keySerializer(Serializer.STRING)	
	 				.valueSerializer(Serializer.INTEGER)
	 				.makeOrGet();
			for(String pr : propertiesSet.keySet()){
				propertiesSetBack.put(pr, propertiesSet.get(pr));
			}
			end = System.nanoTime();
			System.out.println("prop indexes : " + (end-start));
			for(Integer ci : csMap.keySet()){
				result = csMap.get(ci);
				Arrays.sort(result);
				csMap.put(ci, result);
			}
			
			//l.free();
			List<BigExtendedCharacteristicSet> sortECSList = new ArrayList<BigExtendedCharacteristicSet>(uecs.keySet());
			HashMap<BigExtendedCharacteristicSet, List<BigExtendedCharacteristicSet>> outEdges = new HashMap<BigExtendedCharacteristicSet, List<BigExtendedCharacteristicSet>>();
			HashMap<BigExtendedCharacteristicSet, List<BigExtendedCharacteristicSet>> inEdges = new HashMap<BigExtendedCharacteristicSet, List<BigExtendedCharacteristicSet>>();
			Collections.sort(sortECSList, new Comparator<BigExtendedCharacteristicSet>() {
			    public int compare(BigExtendedCharacteristicSet s1, BigExtendedCharacteristicSet s2) {
			        if (s1.getLongRep().cardinality() > s2.getLongRep().cardinality())
			            return 1;    // s1 comes after s2
			        else if (s1.getLongRep().cardinality() < s2.getLongRep().cardinality())
			            return -1;   // s1 comes before s2
			        else {			          
			            return 0;
			        }
			    }
			});
			//build ECS hierarchy graph
			for(BigExtendedCharacteristicSet outer : sortECSList){
				System.out.println("parent: " + outer.getLongRep().toString());
				outEdges.put(outer, new ArrayList<BigExtendedCharacteristicSet>());
				for(BigExtendedCharacteristicSet inner : sortECSList){
					if(outer == inner)
						continue;
					if(outer.getLongRep().cardinality() >= inner.getLongRep().cardinality())
						continue;
					boolean isBreak = false;
					if(isSubset(outer.getLongRep(), inner.getLongRep())){
												
						for(BigExtendedCharacteristicSet childCheck : outEdges.get(outer)){
							if(isSubset(childCheck.getLongRep(), inner.getLongRep())){
								isBreak = true;
							}
						}
						if(isBreak) continue;
						outEdges.get(outer).add(inner);
						System.out.println("child: " + inner.getLongRep().toString());						
						
					}
				}
			}
			
			
			db.close();
			

	}
		

		public static boolean isSubset(BitSet a, BitSet b){
			
			BitSet x = (BitSet) a.clone(), 
					y = (BitSet) b.clone();
			
			x.and(y);
			if(x.equals(a))
				return true;
			return false;
			
		}

		//public static BloomFilter<Long> filter = new InMemoryBloomFilter<Long>(2000000, 0.01);
		public static TLongHashSet filter = new TLongHashSet();
		public static List<Long> result = new ArrayList<Long>();
		public static TIntHashSet visited = new TIntHashSet();
		private static List<Long> join(Map<Integer, ArrayList<Long>> hash, long[] ls2) {
			
			result.clear();
			visited.clear();
			int psub = (int)((ls2[0] >> 27) & 0x7FFFFFF);
			int sub ;
			for(int i = 1; i < ls2.length; i++){
			
				sub = (int)((ls2[i] >> 27) & 0x7FFFFFF);
				//if()
				//if(!visited.contains(sub) && hash.containsKey(sub)){
				if(sub != psub && hash.containsKey(psub)){
				
					result.addAll(hash.get(psub));
					
					filter.addAll(hash.get(psub));
					//visited.add(sub);
				}
				psub = sub;
				
			}
			if(hash.containsKey(psub)){
				
				result.addAll(hash.get(psub));
				
				filter.addAll(hash.get(psub));
				
			}
			
			return result;
			
		}
		
		/* public static List<Long> mergeJoin(long[] left, long[] right)
		    {
		        int l = 0, r = 0;
		        while (l < left.length && r < right.length)
		        {
		            if ((int)((left[l] >> 27) & 0x7FFFFFF) == (int)((right[r] >> 27) & 0x7FFFFFF))
		            {
		                result.add(left[l]);
		                filter.add(left[l]);
		                l++;
		                r++;
		            }
		            else if ((int)((left[l] >> 27) & 0x7FFFFFF) < (int)((right[r] >> 27) & 0x7FFFFFF))
		                l++;
		            else 
		                r++;
		        }
		        return result;
		    }*/
		
		public static int[] convertIntegers(List<Integer> integers)
		{
		    int[] ret = new int[integers.size()];
		    Iterator<Integer> iterator = integers.iterator();
		    for (int i = 0; i < ret.length; i++)
		    {
		        ret[i] = iterator.next().intValue();
		    }
		    return ret;
		}

		public static int indexOfProperty(long[] a, int key) {
			 int lo = 0;
		        int hi = a.length - 1;
		        int firstOccurrence = Integer.MIN_VALUE;
		        while (lo <= hi) {
		        	int mid = lo + (hi - lo) / 2;
		        	
		            int s = (int)((a[mid] >> 54)  & 0x3ff);
		          
		        	 if (s == key) {
		                 // key found and we want to search an earlier occurrence
		                 firstOccurrence = mid;	                 
		                 hi = mid - 1;	                 
		             } else if (s < key) {
		            	 lo = mid + 1;
		             } else {
		            	 hi = mid - 1;
		             }
		            
		        }
		        if (firstOccurrence != Integer.MIN_VALUE) {
		            return firstOccurrence;
		        }

		        return -1;
	    }
		
}
