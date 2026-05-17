package org.example.repositories.impl.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.example.models.User;
import org.example.repositories.UserRepository;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserJsonRepository implements UserRepository {
    private List<User> users = new ArrayList<>();
    private final String FILE_NAME = "users.json";
    private final Gson gson;

    public UserJsonRepository() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        load();
    }

    @Override
    public List<User> findAll() {
        return users.stream()
                .map(User::copy)
                .toList();
    }

    @Override
    public Optional<User> findById(String id) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .map(User::copy)
                .findFirst();
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return users.stream()
                .filter(u -> u.getLogin().equals(login))
                .map(User::copy)
                .findFirst();
    }

    @Override
    public User save(User user) {
        users.removeIf(u -> u.getId().equals(user.getId()));

        User copyToSave = user.copy();
        users.add(copyToSave);
        saveToFile();

        return copyToSave;
    }

    @Override
    public void deleteById(String id) {
        boolean removed = users.removeIf(u -> u.getId().equals(id));
        if (removed) {
            saveToFile();
        }
    }

    @Override
    public void deleteByLogin(String login) {
        boolean removed = users.removeIf(u -> u.getLogin().equals(login));
        if (removed) {
            saveToFile();
        }
    }


    private void saveToFile() {
        try (Writer writer = new FileWriter(FILE_NAME)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            System.err.println("Błąd zapisu użytkowników do JSON: " + e.getMessage());
        }
    }

    private void load() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<ArrayList<User>>(){}.getType();
            List<User> loadedUsers = gson.fromJson(reader, listType);

            if (loadedUsers != null) {
                this.users = loadedUsers;
            }
        } catch (Exception e) {
            System.err.println("Błąd odczytu użytkowników z JSON: " + e.getMessage());
        }
    }
}