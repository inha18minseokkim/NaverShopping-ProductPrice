package com.example.navershoppingproductprice.Utility;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.database.JpaItemWriter;


import java.util.ArrayList;
import java.util.List;

public class JpaListWriter<T> extends JpaItemWriter<List<T>> {
    private final JpaItemWriter<T> itemWriter;
    public JpaListWriter(JpaItemWriter<T> _itemWriter, EntityManagerFactory entityManagerFactory){
        this.itemWriter = _itemWriter;
        this.setEntityManagerFactory(entityManagerFactory);
        itemWriter.setEntityManagerFactory(entityManagerFactory);
    }

    @Override
    public void write(Chunk<? extends List<T>> items) {
        List<T> currentList = new ArrayList<>();
        for(List<T> subList : items){
            currentList.addAll(subList);
        }
        itemWriter.write(new Chunk<>(currentList));
    }
}
