package net.sourcedestination.sai.util;

import net.sourcedestination.funcles.tuple.Tuple2;

import static net.sourcedestination.funcles.tuple.Tuple.makeTuple;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Stream;

public class FunctionUtil {

    /** returns the argument from the collection which maximizes the function f
    *
    * @param inputs all arguments to be evaluated with this function
    * @return the argument from 'inputs' maximizing this function
    */
   public static <F,T extends Comparable<T>> F argmax(Function<F,T> f, 
   		Collection<F> inputs) {
   	return argmax(f, inputs.parallelStream());
   }
   
   /** returns the argument which maximizes this function
   *
   * @param inputs all arguments to be evaluated with this function
   * @return the argument from 'inputs' maximizing this function
   */
  @SafeVarargs
	public static <F, T extends Comparable<T>> F argmax(Function<F,T> f, 
  		F ... inputs) {
      return argmax(f, Arrays.stream(inputs).parallel());
   }
   

   /** returns the argument from the collection which maximizes the function f
    *
    * @param inputs all arguments to be evaluated with this function
    * @return the argument from 'inputs' maximizing this function
    */
   public static <F,T extends Comparable<T>> F argmax(Function<F,T> f, 
   		Stream<F> inputs) {
   	return inputs.map(x -> makeTuple(x, f.apply(x))) 
   			.max(Comparator.comparing(Tuple2::_2)).get()._1();
   }

}
