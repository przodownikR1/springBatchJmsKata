package pl.java.scalatech.writer;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.item.ItemWriter;
@Slf4j
public class SimpleStringWriter implements ItemWriter<String>{

    @Override
    public void write(List<? extends String> items) throws Exception {
           items.stream().forEach(s -> log.info("### {}",s));
        
    }

}
