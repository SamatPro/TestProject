package ru.iis.soft.resporitories;

import java.util.List;
import java.util.Optional;

/**
 * @author Samat Zaydullin
 *
 * */

public interface CrudRepository<T> {
    Optional<T> findOne(Long id);
    /**Сохранение, добавление новых значений*/
    void save(T model);
    /**Удаление из БД*/
    void delete(T model);
    /**Обновление*/
    void update(T model);

}
