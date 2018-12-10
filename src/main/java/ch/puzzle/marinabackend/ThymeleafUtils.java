package ch.puzzle.marinabackend;

import org.springframework.stereotype.Component;

@Component
public class ThymeleafUtils {
    
    public static final double SATOSHI_PER_BITCOIN = 100000000.0d;
    
    public double satoshiToBitcoin(long satoshi) {
        return (double) satoshi / SATOSHI_PER_BITCOIN;
    }
}
