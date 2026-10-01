package school.sptech.leads.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.leads.dto.LeadRequestDto;
import school.sptech.leads.dto.LeadResponseDto;
import school.sptech.leads.entity.Lead;
import school.sptech.leads.mapper.LeadMapper;
import school.sptech.leads.service.LeadService;

import java.util.List;

@RestController
@RequestMapping("/games")
public class LeadController {

    private final LeadService service;


    public LeadController(LeadService service ) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<LeadResponseDto>> listar() {
        //serviço
        List<Lead> games = service.listar();

        //dto
        List<LeadResponseDto> response = LeadMapper.toResponseDto(games);

        //retorno
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeadResponseDto> buscarPorId(@PathVariable Long id) {
        //servico
        Lead game = service.buscarPorId(id);

        //dto
        LeadResponseDto responseDto= LeadMapper.toResponseDto(game);

        //retorno
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping
    public ResponseEntity<LeadResponseDto> criar(@RequestBody @Valid LeadRequestDto dto) {
        //dto
        Lead game = LeadMapper.toEntity(dto);

        //servico
        Lead salvo = service.cadastrarGame(game);

        //dto
        LeadResponseDto responseDto= LeadMapper.toResponseDto(salvo);

        //retorno
        return ResponseEntity.status(201).body(responseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeadResponseDto> atualizar(@PathVariable Long id, @RequestBody @Valid LeadRequestDto dto) {
        //dto
        Lead game = LeadMapper.toEntity(dto);

        //servico
        Lead salvo = service.atualizarGame(game,id);

        //dto
        LeadResponseDto responseDto= LeadMapper.toResponseDto(salvo);

        //retorno
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletarGame(id);
        return ResponseEntity.noContent().build();
    }
}
