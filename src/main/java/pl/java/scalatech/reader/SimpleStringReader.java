package pl.java.scalatech.reader;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
@Slf4j
public class SimpleStringReader implements ItemReader<String>{
    List<String> strs = newArrayList("yamaha", "kawaski", "suzuki", "ducati", "honda", "aprilia", "junak", "romet", "mz", "ktm", "triumph", "bmw");

    @Override
    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        String str = !strs.isEmpty() ? strs.remove(0) : null;
        log.info("+++ reader {}", str);
        return str;
    }
   

}
