package com.smartcontact.service;

import com.smartcontact.entity.Contact;
import com.smartcontact.entity.User;
import com.smartcontact.repository.ContactRepository;
import com.smartcontact.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Contact saveContact(Contact contact, User user) {
        contact.setUser(user);
        user.getContacts().add(contact);
        userRepository.save(user); // Will cascade and save contact
        return contact;
    }

    @Override
    public Contact updateContact(Contact contact) {
        return contactRepository.save(contact);
    }

    @Override
    public Contact getContactById(Long cId) {
        return contactRepository.findById(cId).orElse(null);
    }

    @Override
    public void deleteContact(Long cId, User user) {
        Contact contact = getContactById(cId);
        if (contact != null && contact.getUser().getId().equals(user.getId())) {
            // Decouple contact from user
            user.getContacts().remove(contact);
            contact.setUser(null);
            contactRepository.delete(contact);
        }
    }

    @Override
    public Page<Contact> getContactsByUser(Long userId, Pageable pageable) {
        return contactRepository.findContactsByUser(userId, pageable);
    }

    @Override
    public long countTotalContacts(Long userId) {
        return contactRepository.countContactsByUserId(userId);
    }

    @Override
    public long countFavoriteContacts(Long userId) {
        return contactRepository.countFavoriteContactsByUserId(userId);
    }

    @Override
    public List<Contact> getRecentlyAdded(Long userId) {
        return contactRepository.findTop5ByUser_IdOrderBycIdDesc(userId, org.springframework.data.domain.PageRequest.of(0, 5));
    }

    @Override
    public List<Object[]> getCategoryStats(Long userId) {
        return contactRepository.countContactsByCategoryGrouped(userId);
    }

    @Override
    public Page<Contact> searchGlobal(Long userId, String query, Pageable pageable) {
        return contactRepository.searchGlobal(userId, query, pageable);
    }

    @Override
    public Page<Contact> searchMultiFilter(Long userId, String name, String phone, String email, String work, String category, Boolean favorite, Pageable pageable) {
        return contactRepository.searchMultiFilter(userId, name, phone, email, work, category, favorite, pageable);
    }

    @Override
    public List<Contact> getAllContactsForUser(Long userId) {
        return contactRepository.findByUser_Id(userId);
    }
}
