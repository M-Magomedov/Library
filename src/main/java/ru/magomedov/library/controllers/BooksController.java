package ru.magomedov.library.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.magomedov.library.dao.BookDAO;
import ru.magomedov.library.dao.PersonDAO;
import ru.magomedov.library.models.Book;
import ru.magomedov.library.models.Person;

import javax.validation.Valid;
import java.util.Optional;


@Controller
@RequestMapping("/books")
public class BooksController {

    private final BookDAO bookDAO;
    private final PersonDAO personDAO;

    @Autowired
    public BooksController(BookDAO BookDAO, PersonDAO personDAO) {
        this.bookDAO = BookDAO;
        this.personDAO = personDAO;
    }

    @GetMapping()
    public String index(Model model) {   //получить все книги
        model.addAttribute("books", bookDAO.index());
        return "books/index";
    }

    @GetMapping("/{id}")   //получить книгу по id
    public String show(@PathVariable("id") int id, Model model, @ModelAttribute("person") Person person) {
        model.addAttribute("book", bookDAO.show(id));

        Optional<Person> bookOwner = bookDAO.getBookOwner(id);

        if (bookOwner.isPresent())
            model.addAttribute("owner", bookOwner.get());
        else
            model.addAttribute("people", personDAO.index());

        return "books/show";
    }

    @GetMapping("/new")  //добавление новой книги
    public String newBook(@ModelAttribute("book") Book Book) {
        return "books/new";
    }

    @PostMapping()   //добавить новую книгу в БД
    public String create(@ModelAttribute("book") @Valid Book Book,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "books/new";

        bookDAO.save(Book);
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")   //получить книгу, которую необходимо изменить
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("book", bookDAO.show(id));
        return "books/edit";
    }

    @PatchMapping("/{id}")  //изменить книгу и внести изменения в БД
    public String update(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult,
                         @PathVariable("id") int id) {
        if (bindingResult.hasErrors())
            return "books/edit";

        bookDAO.update(id, book);
        return "redirect:/books";
    }

    @DeleteMapping("/{id}")  //удалить книгу
    public String delete(@PathVariable("id") int id) {
        bookDAO.delete(id);
        return "redirect:/books";
    }

    @PatchMapping("/{id}/release")   // Освободить книгу, когда человек возвращает книгу в библиотеку
    public String release(@PathVariable("id") int id) {
        bookDAO.release(id);
        return "redirect:/books/" + id;
    }

    @PatchMapping("/{id}/assign")   // Назначить книгу человеку (когда человек забирает книгу из библиотеки)
    public String assign(@PathVariable("id") int id, @ModelAttribute("person") Person selectedPerson) {
        bookDAO.assign(id, selectedPerson);
        return "redirect:/books/" + id;
    }
}
