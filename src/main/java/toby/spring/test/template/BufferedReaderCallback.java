package toby.spring.test.template;

import java.io.BufferedReader;
import java.io.IOException;

// BufferedReader를 전달받는 콜백 인터페이스
public interface BufferedReaderCallback {
    Integer doSomethingWithBufferedReader(BufferedReader br) throws IOException;
}
