package ir.lms.service;

public interface InitializerService {
    void createAdminIfNotExists();
    void createRolesIfNotExist();
}
