
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BijectionGroup {

    public interface Bijection<T, Y> extends Function<T, Y>{
        String name();
        Set<T> input();
        Set<Y> output();
    }


    private static <T> List<List<T>> permutation(List<T> domain) {
        T permutationElement;
        if (!domain.isEmpty()) {
            permutationElement = domain.get(0);
            domain.remove(0);
        } else{
            List<List<T>> toReturn = new ArrayList<>();
            toReturn.add(new ArrayList<>());
            return toReturn;
        }

        List<List<T>> copy = permutation(domain);

        List<List<T>> sendCopy = new ArrayList<>();

        //System.out.println(permutationElement + "[erm element");

        for (List<T> perm : copy) {
            //System.out.println(perm.size());
            for (int i = 0; i <= perm.size(); i++) {
                List<T> temp = new ArrayList<>(perm);
                temp.add(i, permutationElement);
                sendCopy.add(temp);
            }
        }
        return sendCopy;

    }

    public static <T> Set<Bijection<T,T>> bijectionsOf(Set<T> domain){
        List<List<T>> remainingSets = permutation(new ArrayList<>(domain));
        //Set<Bijection<T,T>> bijectionsSet = Stream.of()

        List<Bijection<T,T>> toConvert = new ArrayList<>();

        for(List<T> perm : remainingSets) {

            Bijection<T, T> a = new Bijection<T, T>() {
                List<T> tempInput = new ArrayList<>(domain);
                List<T> tempOutput = new ArrayList<>(perm);


                //For every permutation of the list create a bijection that maps the domain to the permutation.

                @Override
                public String name() {
                    return "bijection";
                }

                @Override
                public Set<T> input() {
                    return domain;
                }


                //Output set will depend on what is applied to get the different permutation set.
                @Override
                public Set<T> output() {
                    Set<T> output = tempOutput.stream().collect(Collectors.toSet());
                    return output;
                }

                @Override
                public T apply(T t) {
                    List<T> toApply = new ArrayList<>(tempOutput);
                    if (toApply.size() == 0)
                        throw new IllegalArgumentException();
                    int index = tempInput.indexOf(t);
                    return tempOutput.get(index);
                }

            };

            toConvert.add(a);

        }

            Set<Bijection<T,T>> bijectionsSet = toConvert.stream().collect(Collectors.toSet());

            return bijectionsSet;

    }

    public static <T> Group<Bijection<T,T>> bijectionGroup(Set<T> domain){
        Group<Bijection<T,T>> group = new Group<Bijection<T, T>>() {

            final Set<Bijection<T,T>> bijectionsOf = bijectionsOf(domain);

            @Override
            public Bijection<T, T> binaryOperation(Bijection<T, T> one, Bijection<T, T> other) {

                Bijection<T,T> compositeFunction = new Bijection<T, T>() {
                    @Override
                    public String name() {
                        return "binaryOperation";
                    }

                    @Override
                    public Set<T> input() {
                        return domain;
                    }

                    @Override
                    public Set<T> output() {
                        List<T> tempOutput = new ArrayList<>();
                        for(T element : domain)
                            tempOutput.add(apply(element));


                        return tempOutput.stream().collect(Collectors.toSet());
                    }

                    @Override
                    public T apply(T t) {
                        return one.apply(other.apply(t));
                    }
                };

                return compositeFunction;
            }

            @Override
            public Bijection<T, T> identity() {
                for(Bijection<T,T> bijection : bijectionsOf){
                    List<T> toCompare = new ArrayList<>(domain);
                    List<T> convert = new ArrayList<>();

                    for(T element : toCompare){
                        convert.add(bijection.apply(element));
                    }

                    if(toCompare.equals(convert))
                        return bijection;
                }

                return null;
            }

            @Override
            public Bijection<T, T> inverseOf(Bijection<T, T> Bijection) {
                List<T> baseOutcome = new ArrayList<>();
                List<T> leadingDomain = new ArrayList<>();

                for(T element : domain){
                    baseOutcome.add(Bijection.apply(element));
                    leadingDomain.add(element);
                }

                for(Bijection<T,T> bijections : bijectionsOf){
                    boolean allTrue = true;

                    for(int i = 0; i<baseOutcome.size(); i++){
                        if(bijections.apply(baseOutcome.get(i)) != leadingDomain.get(i)) {
                            allTrue = false;
                        }
                    }

                    if(allTrue)
                        return bijections;
                }
                return null;
            }


        };

        return group;
    }


    public static void main(String... args) {
        //Bijections of test

        Set<Integer> a_few = Stream.of(1, 2, 3).collect(Collectors.toSet());
// you have to figure out the data type in the line below

        Set<Bijection<Integer, Integer>> bijections = bijectionsOf(a_few);

        bijections.forEach(aBijection -> {
            a_few.forEach(n -> System.out.printf("%d --> %d; ", n, aBijection.apply(n)));
            System.out.println();
        });


        //BijectionGroup test

        System.out.println("Original test complete");

// you have to figure out the data types in the lines below
        Group<Bijection<Integer, Integer>> g = bijectionGroup(a_few);


        bijections.forEach(aBijection -> {
            System.out.println("Base:");
            a_few.forEach(n -> System.out.printf("%d --> %d; ", n, aBijection.apply(n)));
            System.out.println("\n" + "Inverse");
            a_few.forEach(n -> System.out.printf("%d --> %d; ", n, g.inverseOf(aBijection).apply(n)));
            System.out.println("\n" +"Identity");
            a_few.forEach(n -> System.out.printf("%d --> %d; ", n, g.identity().apply(n)));
            System.out.println("\n" +"Next Bijection");
        });

        System.out.println("BinaryOperation test: " + "\n");
        List<Bijection<Integer, Integer>> bBijections = new ArrayList<>(bijections);

        for(int i = 0; i<bBijections.size() -1; i++){
            int finalI = i;
            a_few.forEach(n -> System.out.printf("%d --> %d; ", n, g.binaryOperation(bBijections.get(finalI),bBijections.get(finalI +1)).apply(n)));
            System.out.println();
        }

    }

}

