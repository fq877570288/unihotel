package cn.cucsi.bsd.ucc.service.impl;

import cn.cucsi.bsd.ucc.common.beans.UccUserCriteria;
import cn.cucsi.bsd.ucc.common.mapper.TeamUsersMapper;
import cn.cucsi.bsd.ucc.common.mapper.UccUsersMapper;
import cn.cucsi.bsd.ucc.data.domain.*;
import cn.cucsi.bsd.ucc.data.repo.TeamUsersRepository;
import cn.cucsi.bsd.ucc.data.repo.UccUserRepository;
import cn.cucsi.bsd.ucc.data.specs.TeamUsersSpecs;
import cn.cucsi.bsd.ucc.service.TeamUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeamUsersServiceImpl implements TeamUsersService{

    @Autowired
    private TeamUsersRepository teamUsersRepository;
    @Autowired
    private UccUserRepository uccUserRepository;
    @Autowired
    private TeamUsersMapper teamUsersMapper;
    @Autowired
    private UccUsersMapper uccUsersMapper;

    @Override
    public List<UccUsers> findAll(UccUserCriteria criteria) {
        return uccUsersMapper.selectTeamUserByDomainId(criteria);
    }

    @Override
    public List<UccUsers> addFindAll(UccUserCriteria criteria) {
        return uccUsersMapper.selectNoTeamUserByDomainId(criteria);
    }

    @Override
    public UccUsers findOne(String userId) {
        return this.uccUserRepository.findOne(userId);
    }

    @Override
    public TeamUsers save(TeamUsers teamUsers) {
        return this.teamUsersRepository.save(teamUsers);
    }

    @Override
    public Boolean delete(String userId) {
        this.teamUsersRepository.delete(userId);
        return true;
    }

    @Override
    public Integer deleteByPrimaryKey(TeamUsers teamUsers) {
        return teamUsersMapper.deleteByPrimaryKey(teamUsers);
    }

}
