package com.devsuperior.dslist.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dslist.dto.GameListDTO;
import com.devsuperior.dslist.entities.GameList;
import com.devsuperior.dslist.projections.GameMinProjection;
import com.devsuperior.dslist.repositories.GameListRepository;
import com.devsuperior.dslist.repositories.GameRepository;

@Service
public class GameListService {
	@Autowired
	private GameListRepository gameListRepository;
	
	@Autowired
	private GameRepository gameRepository;
	
	@Transactional(readOnly = true)
	public List<GameListDTO> findAll() {
		List<GameList> result = gameListRepository.findAll();
		return result.stream().map(x -> new GameListDTO(x)).toList();
	}
	
	// Endpoint para reordenar um jogo na lista
	// Para reordenar, um jogo sairá do índice source e irá para o índice destination
	@Transactional
	public void move(Long listId, int sourceIndex, int destinationIndex) {
		// Busca a lista ordenada no banco
		List<GameMinProjection> list = gameRepository.searchByList(listId);
		
		// Objeto com o jogo removido da lista
		GameMinProjection obj = list.remove(sourceIndex);
		list.add(destinationIndex, obj);
		
		// Pega o mínimo e o máximo entre os índices de origem e destino, pra saber quais posições da lista irá atualizar
		int min = sourceIndex < destinationIndex ? sourceIndex : destinationIndex;
		int max = sourceIndex < destinationIndex ? destinationIndex : sourceIndex;
		
		for (int i = min; i <= max; i++) {
			gameListRepository.updateBelongingPosition(listId, list.get(i).getId(), i);
		}
	}
}