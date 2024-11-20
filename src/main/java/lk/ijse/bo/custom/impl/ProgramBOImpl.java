package lk.ijse.bo.custom.impl;

import lk.ijse.bo.custom.ProgramBO;
import lk.ijse.dao.DAOFactory;
import lk.ijse.dao.custom.ProgramDAO;
import lk.ijse.dto.ProgramDTO;
import lk.ijse.entity.Program;

import java.util.ArrayList;
import java.util.List;

public class ProgramBOImpl implements ProgramBO {

    private final ProgramDAO programDAO = (ProgramDAO) DAOFactory.getInstance()
            .getDAO(DAOFactory.DAOTypes.PROGRAM);

    @Override
    public boolean saveProgram(ProgramDTO dto) throws Exception {
        return programDAO.save(new Program(
                dto.getProgramId(),
                dto.getProgramName(),
                dto.getDurationMonths(),
                dto.getFee(),
                dto.getDescription()
        ));
    }

    @Override
    public boolean updateProgram(ProgramDTO dto) throws Exception {
        return programDAO.update(new Program(
                dto.getProgramId(),
                dto.getProgramName(),
                dto.getDurationMonths(),
                dto.getFee(),
                dto.getDescription()
        ));
    }

    @Override
    public boolean deleteProgram(String id) throws Exception {
        return programDAO.delete(id);
    }

    @Override
    public ProgramDTO searchProgram(String id) throws Exception {
        Program program = programDAO.search(id);
        if (program != null) {
            return new ProgramDTO(
                    program.getProgramId(),
                    program.getProgramName(),
                    program.getDurationMonths(),
                    program.getFee(),
                    program.getDescription()
            );
        }
        return null;
    }

    @Override
    public List<ProgramDTO> getAllPrograms() throws Exception {
        List<Program> programs = programDAO.getAll();
        List<ProgramDTO> programDTOS = new ArrayList<>();

        for (Program program : programs) {
            programDTOS.add(new ProgramDTO(
                    program.getProgramId(),
                    program.getProgramName(),
                    program.getDurationMonths(),
                    program.getFee(),
                    program.getDescription()
            ));
        }
        return programDTOS;
    }

    @Override
    public String getNextProgramId() throws Exception {
        String lastId = programDAO.getLastId();
        if (lastId == null) {
            return "CA1001";
        }

        int numericPart = Integer.parseInt(lastId.replace("CA", ""));
        return String.format("CA%04d", (numericPart + 1));
    }

    @Override
    public boolean existsByProgramId(String id) throws Exception {
        return programDAO.existsById(id);
    }
}