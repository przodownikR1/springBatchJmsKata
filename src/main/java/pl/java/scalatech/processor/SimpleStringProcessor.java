package pl.java.scalatech.processor;

import org.springframework.batch.item.ItemProcessor;

public class SimpleStringProcessor implements ItemProcessor<String, String> {

    @Override
    public String process(String item) throws Exception {
        return item + "_" + item.length();
    }

}
