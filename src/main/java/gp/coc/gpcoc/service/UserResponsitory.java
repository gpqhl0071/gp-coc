package gp.coc.gpcoc.service;

import org.springframework.data.repository.CrudRepository;

import gp.coc.gpcoc.bean.UserBean;

public interface UserResponsitory extends CrudRepository<UserBean, Long> {
}
