//import static org.junit.Assert .*;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.ArrayList;
//
////tests each possible hand state
//public class HandEvalTest {
//    //@Override
//
//    //public static ArrayList<Card> hand = new ArrayList<>();
//    //public static ArrayList<Card> flop = new ArrayList<>();
//
//
//    public static HandEval H = new HandEval();
//
//    @Test
//    public void TestRoyalFlush(){
//       // System.out.println("Lol");
//        // flop.get(0).
//        ArrayList<Card> hand = new ArrayList<>();
//        ArrayList<Card> flop = new ArrayList<>();
//
//        Card card1 = new Card();
//        card1.suit = 3;
//        card1.rank = 14;
//        Card card2 = new Card();
//        card2.suit = 3;
//        card2.rank = 13;
//        Card card3 = new Card();
//        card3.suit = 3;
//        card3.rank = 12;
//        Card card4 = new Card();
//        card4.suit = 3;
//        card4.rank = 11;
//        Card card5 = new Card();
//        card5.suit = 4;
//        card5.rank = 3;
//
//        Card card6 = new Card();
//        card6.suit = 3;
//        card6.rank = 10;
//
//        Card card7 = new Card();
//        card7.suit = 3;
//        card7.rank = 6;
//
//
//        flop = Services.insert(flop, card1);
//        flop = Services.insert(flop, card2);
//        flop = Services.insert(flop, card6);
//        flop = Services.insert(flop, card7);
//        flop = Services.insert(flop, card3);
//
//        flop = Services.insert(flop, card4);
//        flop = Services.insert(flop, card5);
//
//        //hand = Services.insert(hand, card4);
//        //hand = Services.insert(hand, card5);
//
//        //for(int i = 0 ; i < 5; i++){
//        //    System.out.println(Integer.toString(flop.get(i).rank));
//        //}
//        //Assertions.assertEquals(14 , H.straight(flop, 3));
//        assertArrayEquals(new int[] {5,14}, H.evaluate(hand, flop));
//    }
//    @Test //just a flush
//    public void TestRoyalFlush2(){
//        // flop.get(0).
//        ArrayList<Card> hand = new ArrayList<>();
//        ArrayList<Card> flop = new ArrayList<>();
//
//        Card card1 = new Card();
//        card1.suit = 2;
//        card1.rank = 14;
//        Card card2 = new Card();
//        card2.suit = 3;
//        card2.rank = 13;
//        Card card3 = new Card();
//        card3.suit = 3;
//        card3.rank = 12;
//        Card card4 = new Card();
//        card4.suit = 3;
//        card4.rank = 11;
//        Card card5 = new Card();
//        card5.suit = 4;
//        card5.rank = 3;
//
//        Card card6 = new Card();
//        card6.suit = 3;
//        card6.rank = 10;
//
//        Card card7 = new Card();
//        card7.suit = 3;
//        card7.rank = 6;
//
//
//        flop = Services.insert(flop, card1);
//        flop = Services.insert(flop, card2);
//        flop = Services.insert(flop, card6);
//        flop = Services.insert(flop, card7);
//        flop = Services.insert(flop, card3);
//
//        flop = Services.insert(flop, card4);
//        flop = Services.insert(flop, card5);
//
//        //hand = Services.insert(hand, card4);
//        //hand = Services.insert(hand, card5);
//        //Assertions.assertEquals(14 , H.straight(flop, 3));
//        assertArrayEquals(new int[] {5,13}, H.evaluate(hand, flop));
//    }
//    @Test
//    public void TestStraightFlushLowAce(){
//        // flop.get(0).
//        ArrayList<Card> hand = new ArrayList<>();
//        ArrayList<Card> flop = new ArrayList<>();
//
//        Card card1 = new Card();
//        card1.suit = 3;
//        card1.rank = 14;
//        Card card2 = new Card();
//        card2.suit = 3;
//        card2.rank = 2;
//        Card card3 = new Card();
//        card3.suit = 3;
//        card3.rank = 3;
//        Card card4 = new Card();
//        card4.suit = 3;
//        card4.rank = 4;
//        Card card5 = new Card();
//        card5.suit = 4;
//        card5.rank = 3;
//
//        Card card6 = new Card();
//        card6.suit = 3;
//        card6.rank = 5;
//
//        Card card7 = new Card();
//        card7.suit = 3;
//        card7.rank = 6;
//
//
//        flop = Services.insert(flop, card1);
//        flop = Services.insert(flop, card2);
//        flop = Services.insert(flop, card6);
//        flop = Services.insert(flop, card7);
//        flop = Services.insert(flop, card3);
//
//        //flop = Services.insert(flop, card4);
//        //flop = Services.insert(flop, card5);
//
//        hand = Services.insert(hand, card4);
//        hand = Services.insert(hand, card5);
//        //Assertions.assertEquals(14 , H.straight(flop, 3));
//        assertArrayEquals(new int[] {8,14}, H.evaluate(hand, flop));
//    }
//
//    @Test
//    public void TestStraightFlush(){
//        // flop.get(0).
//        ArrayList<Card> hand = new ArrayList<>();
//        ArrayList<Card> flop = new ArrayList<>();
//
//        Card card1 = new Card();
//        card1.suit = 2;
//        card1.rank = 14;
//        Card card2 = new Card();
//        card2.suit = 3;
//        card2.rank = 2;
//        Card card3 = new Card();
//        card3.suit = 3;
//        card3.rank = 3;
//        Card card4 = new Card();
//        card4.suit = 3;
//        card4.rank = 4;
//        Card card5 = new Card();
//        card5.suit = 3;
//        card5.rank = 7;
//
//        Card card6 = new Card();
//        card6.suit = 3;
//        card6.rank = 5;
//
//        Card card7 = new Card();
//        card7.suit = 3;
//        card7.rank = 6;
//
//
//        flop = Services.insert(flop, card1);
//        flop = Services.insert(flop, card2);
//        flop = Services.insert(flop, card6);
//        flop = Services.insert(flop, card7);
//        flop = Services.insert(flop, card3);
//
//        //flop = Services.insert(flop, card4);
//        //flop = Services.insert(flop, card5);
//
//        hand = Services.insert(hand, card4);
//        hand = Services.insert(hand, card5);
//
//
//        //Assertions.assertEquals(14 , H.straight(flop, 3));
//        assertArrayEquals(new int[] {5,7}, H.evaluate(hand, flop));
//    }
//
//    @Test
//    public void TestFourOfAKind(){
//        // flop.get(0).
//        ArrayList<Card> hand = new ArrayList<>();
//        ArrayList<Card> flop = new ArrayList<>();
//
//        Card card1 = new Card();
//        card1.suit = 3;
//        card1.rank = 14;
//        Card card2 = new Card();
//        card2.suit = 2;
//        card2.rank = 14;
//        Card card3 = new Card();
//        card3.suit = 1;
//        card3.rank = 14;
//        Card card4 = new Card();
//        card4.suit = 4;
//        card4.rank = 14;
//        Card card5 = new Card();
//        card5.suit = 4;
//        card5.rank = 3;
//
//        Card card6 = new Card();
//        card6.suit = 3;
//        card6.rank = 3;
//
//        Card card7 = new Card();
//        card7.suit = 2;
//        card7.rank = 3;
//
//
//        flop = Services.insert(flop, card1);
//        flop = Services.insert(flop, card2);
//        flop = Services.insert(flop, card6);
//        flop = Services.insert(flop, card7);
//        flop = Services.insert(flop, card3);
//
//        //flop = Services.insert(flop, card4);
//        //flop = Services.insert(flop, card5);
//
//        hand = Services.insert(hand, card4);
//        hand = Services.insert(hand, card5);
//
//        //Assertions.assertEquals(14 , H.straight(flop, 3));
//        assertArrayEquals(new int[] {7,14}, H.evaluate(hand, flop));
//    }
//
//    @Test
//    public void TestFullHouse(){
//        // flop.get(0).
//        ArrayList<Card> hand = new ArrayList<>();
//        ArrayList<Card> flop = new ArrayList<>();
//
//        Card card1 = new Card();
//        card1.suit = 3;
//        card1.rank = 14;
//        Card card2 = new Card();
//        card2.suit = 2;
//        card2.rank = 14;
//        Card card3 = new Card();
//        card3.suit = 1;
//        card3.rank = 14;
//        Card card4 = new Card();
//        card4.suit = 4;
//        card4.rank = 2;
//        Card card5 = new Card();
//        card5.suit = 4;
//        card5.rank = 3;
//
//        Card card6 = new Card();
//        card6.suit = 3;
//        card6.rank = 3;
//
//        Card card7 = new Card();
//        card7.suit = 2;
//        card7.rank = 3;
//
//
//        flop = Services.insert(flop, card1);
//        flop = Services.insert(flop, card2);
//        flop = Services.insert(flop, card6);
//        flop = Services.insert(flop, card7);
//        flop = Services.insert(flop, card3);
//
//        //flop = Services.insert(flop, card4);
//        //flop = Services.insert(flop, card5);
//
//        hand = Services.insert(hand, card4);
//        hand = Services.insert(hand, card5);
//
//        //Assertions.assertEquals(14 , H.straight(flop, 3));
//        assertArrayEquals(new int[] {6,14}, H.evaluate(hand, flop));
//    }
//
//    @Test
//    public void TestStraight(){
//        // flop.get(0).
//        ArrayList<Card> hand = new ArrayList<>();
//        ArrayList<Card> flop = new ArrayList<>();
//
//        Card card1 = new Card();
//        card1.suit = 3;
//        card1.rank = 14;
//        Card card2 = new Card();
//        card2.suit = 2;
//        card2.rank = 6;
//        Card card3 = new Card();
//        card3.suit = 1;
//        card3.rank = 14;
//        Card card4 = new Card();
//        card4.suit = 4;
//        card4.rank = 4;
//        Card card5 = new Card();
//        card5.suit = 4;
//        card5.rank = 3;
//
//        Card card6 = new Card();
//        card6.suit = 3;
//        card6.rank = 5;
//
//        Card card7 = new Card();
//        card7.suit = 2;
//        card7.rank = 7;
//
//
//        flop = Services.insert(flop, card1);
//        flop = Services.insert(flop, card2);
//        flop = Services.insert(flop, card6);
//        flop = Services.insert(flop, card7);
//        flop = Services.insert(flop, card3);
//
//        //flop = Services.insert(flop, card4);
//        //flop = Services.insert(flop, card5);
//
//        hand = Services.insert(hand, card4);
//        hand = Services.insert(hand, card5);
//
//        //Assertions.assertEquals(14 , H.straight(flop, 3));
//        assertArrayEquals(new int[] {4,7}, H.evaluate(hand, flop));
//    }
//
//    @Test
//    public void TestTriple(){
//        // flop.get(0).
//        ArrayList<Card> hand = new ArrayList<>();
//        ArrayList<Card> flop = new ArrayList<>();
//
//        Card card1 = new Card();
//        card1.suit = 3;
//        card1.rank = 14;
//        Card card2 = new Card();
//        card2.suit = 2;
//        card2.rank = 14;
//        Card card3 = new Card();
//        card3.suit = 1;
//        card3.rank = 14;
//        Card card4 = new Card();
//        card4.suit = 4;
//        card4.rank = 2;
//        Card card5 = new Card();
//        card5.suit = 4;
//        card5.rank = 5;
//
//        Card card6 = new Card();
//        card6.suit = 3;
//        card6.rank = 6;
//
//        Card card7 = new Card();
//        card7.suit = 2;
//        card7.rank = 3;
//
//
//        flop = Services.insert(flop, card1);
//        flop = Services.insert(flop, card2);
//        flop = Services.insert(flop, card6);
//        flop = Services.insert(flop, card7);
//        flop = Services.insert(flop, card3);
//
//        //flop = Services.insert(flop, card4);
//        //flop = Services.insert(flop, card5);
//
//        hand = Services.insert(hand, card4);
//        hand = Services.insert(hand, card5);
//
//        //Assertions.assertEquals(14 , H.straight(flop, 3));
//        assertArrayEquals(new int[] {3,14}, H.evaluate(hand, flop));
//    }
//
//    @Test
//    public void TestTwoPairs(){
//        // flop.get(0).
//        ArrayList<Card> hand = new ArrayList<>();
//        ArrayList<Card> flop = new ArrayList<>();
//
//        Card card1 = new Card();
//        card1.suit = 3;
//        card1.rank = 14;
//        Card card2 = new Card();
//        card2.suit = 2;
//        card2.rank = 5;
//        Card card3 = new Card();
//        card3.suit = 1;
//        card3.rank = 2;
//        Card card4 = new Card();
//        card4.suit = 4;
//        card4.rank = 3;
//        Card card5 = new Card();
//        card5.suit = 3;
//        card5.rank = 3;
//
//        Card card6 = new Card();
//        card6.suit = 3;
//        card6.rank = 5;
//
//        Card card7 = new Card();
//        card7.suit = 2;
//        card7.rank = 2;
//
//
//        flop = Services.insert(flop, card1);
//        flop = Services.insert(flop, card2);
//        flop = Services.insert(flop, card6);
//        flop = Services.insert(flop, card7);
//        flop = Services.insert(flop, card3);
//
//        //flop = Services.insert(flop, card4);
//        //flop = Services.insert(flop, card5);
//
//        hand = Services.insert(hand, card4);
//        hand = Services.insert(hand, card5);
//
//        //Assertions.assertEquals(14 , H.straight(flop, 3));
//        assertArrayEquals(new int[] {2,5}, H.evaluate(hand, flop));
//    }
//
//    @Test
//    public void TestOnePair(){
//        // flop.get(0).
//        ArrayList<Card> hand = new ArrayList<>();
//        ArrayList<Card> flop = new ArrayList<>();
//
//        Card card1 = new Card();
//        card1.suit = 3;
//        card1.rank = 12;
//        Card card2 = new Card();
//        card2.suit = 2;
//        card2.rank = 9;
//        Card card3 = new Card();
//        card3.suit = 1;
//        card3.rank = 6;
//        Card card4 = new Card();
//        card4.suit = 4;
//        card4.rank = 3;
//        Card card5 = new Card();
//        card5.suit = 3;
//        card5.rank = 3;
//
//        Card card6 = new Card();
//        card6.suit = 3;
//        card6.rank = 5;
//
//        Card card7 = new Card();
//        card7.suit = 2;
//        card7.rank = 2;
//
//
//        flop = Services.insert(flop, card1);
//        flop = Services.insert(flop, card2);
//        flop = Services.insert(flop, card6);
//        flop = Services.insert(flop, card7);
//        flop = Services.insert(flop, card3);
//
//        //flop = Services.insert(flop, card4);
//        //flop = Services.insert(flop, card5);
//
//        hand = Services.insert(hand, card4);
//        hand = Services.insert(hand, card5);
//
//        //Assertions.assertEquals(14 , H.straight(flop, 3));
//        assertArrayEquals(new int[] {1,3}, H.evaluate(hand, flop));
//    }
//
//    @Test
//    public void TestHighCard(){
//        // flop.get(0).
//        ArrayList<Card> hand = new ArrayList<>();
//        ArrayList<Card> flop = new ArrayList<>();
//
//        Card card1 = new Card();
//        card1.suit = 3;
//        card1.rank = 9;
//        Card card2 = new Card();
//        card2.suit = 2;
//        card2.rank = 5;
//        Card card3 = new Card();
//        card3.suit = 1;
//        card3.rank = 2;
//        Card card4 = new Card();
//        card4.suit = 4;
//        card4.rank = 3;
//        Card card5 = new Card();
//        card5.suit = 3;
//        card5.rank = 7;
//
//        Card card6 = new Card();
//        card6.suit = 3;
//        card6.rank = 8;
//
//        Card card7 = new Card();
//        card7.suit = 2;
//        card7.rank = 4;
//
//
//        flop = Services.insert(flop, card1);
//        flop = Services.insert(flop, card2);
//        flop = Services.insert(flop, card6);
//        flop = Services.insert(flop, card7);
//        flop = Services.insert(flop, card3);
//
//        //flop = Services.insert(flop, card4);
//        //flop = Services.insert(flop, card5);
//
//        hand = Services.insert(hand, card4);
//        hand = Services.insert(hand, card5);
//
//        //Assertions.assertEquals(14 , H.straight(flop, 3));
//        assertArrayEquals(new int[] {0,9}, H.evaluate(hand, flop));
//    }
//}