package com.oingmaryho.business.hubservice.infrastructure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.oingmaryho.business.hubservice.domain.HubRoute;
import com.oingmaryho.business.hubservice.domain.RouteInfo;
import com.oingmaryho.business.hubservice.domain.service.ShortestPathService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DijkstraServiceImpl implements ShortestPathService {

	// 허브 간 연결 관계
	private static final Map<String, Set<String>> HUB_CONNECTIONS;

	static {
		HUB_CONNECTIONS = new HashMap<>();
		HUB_CONNECTIONS.put("경기 남부 센터",
			new HashSet<>(Arrays.asList("경기 북부 센터", "서울특별시 센터", "인천광역시 센터", "강원도특별자치도 센터", "경상북도 센터", "대전광역시 센터", "대구")));
		HUB_CONNECTIONS.put("대전광역시 센터",
			new HashSet<>(Arrays.asList("충청남도 센터", "충청북도 센터", "세종특별자치시 센터", "전북특별자치도 센터", "광주광역시 센터", "전라남도 센터", "경기 남부 센터", "대구광역시 센터")));
		HUB_CONNECTIONS.put("대구광역시 센터", new HashSet<>(Arrays.asList("경상북도 센터", "경상남도 센터", "부산광역시 센터", "울산광역시 센터", "경기 남부 센터", "대전광역시 센터")));
		HUB_CONNECTIONS.put("경상북도 센터", new HashSet<>(Arrays.asList("경기 남부 센터", "대구광역시 센터")));
	}

	//SSSP 알고리즘
	@Override
	public List<HubRoute> getShortestPath(UUID departureHubId, UUID arriveHubId, List<HubRoute> allHubRoutes,
		Map<UUID, String> hubNameMatcher) {
		// 허브 ID 목록 생성
		Set<UUID> uniqueHubIds = new HashSet<>();
		for (HubRoute route : allHubRoutes) {
			uniqueHubIds.add(route.getDepartureHubId());
			uniqueHubIds.add(route.getArriveHubId());
		}
		List<UUID> hubIds = new ArrayList<>(uniqueHubIds);

		HubNetwork hubNetwork = new HubNetwork(hubIds);

		// 간선 추가 (연결된 허브만 추가)
		for (HubRoute route : allHubRoutes) {
			String fromName = hubNameMatcher.get(route.getDepartureHubId());
			String toName = hubNameMatcher.get(route.getArriveHubId());
			if (isConnected(fromName, toName)) {
				hubNetwork.addEdge(route.getDepartureHubId(), route.getArriveHubId(),
					route.getRouteInfo().getHubToHubTime(),
					route.getRouteInfo().getDistance());
			} else {
				log.info("경로 제외: {} -> {} (연결되지 않은 경로 제외)", fromName, toName);
			}

		}

		// 직선 경로 계산
		PathResult directPath = hubNetwork.dijkstra(departureHubId, arriveHubId, new HashSet<>());
		// 경유지 포함 경로 계산
		PathResult relayPath = hubNetwork.findPathWithOptimizeRelay(departureHubId, arriveHubId);

		// 최적 경로 선택
		PathResult result = (directPath.totalDistance == -1 && relayPath.totalDistance == -1)
			? new PathResult(new ArrayList<>(), -1, -1.0)
			: (directPath.totalDistance == -1 ||
			(relayPath.totalDistance != -1 &&
				(relayPath.totalDistance < directPath.totalDistance ||
					(relayPath.totalDistance == directPath.totalDistance &&
						relayPath.totalTime < directPath.totalTime))))
			? relayPath : directPath;

		// 경로가 없을 때
		if (result.path.isEmpty()) {
			log.info("경로를 찾을 수 없습니다: {} -> {}", hubNameMatcher.get(departureHubId), hubNameMatcher.get(arriveHubId));
			return Collections.emptyList();
		}

		// 원본 경로 매핑
		Map<String, HubRoute> routeMap = allHubRoutes.stream()
			.collect(Collectors.toMap(
				r -> r.getDepartureHubId() + ":" + r.getArriveHubId(),
				r -> r,
				(r1, r2) -> r1
			));

		// 최단 경로 -> HubRoute list로 변환
		List<HubRoute> shortestPath = new ArrayList<>();
		for (int i = 0; i < result.path.size() - 1; i++) {
			UUID from = result.path.get(i);
			UUID to = result.path.get(i + 1);
			String key = from + ":" + to;
			HubRoute route = routeMap.get(key);
			if (route != null) {
				shortestPath.add(route);
			} else {
				ArrivalHub edge = hubNetwork.adjList.get(hubNetwork.hubIndex.get(from)).get(to);
				RouteInfo routeInfo = new RouteInfo(edge.hubToHubTime, edge.distance);
				shortestPath.add(HubRoute.builder()
					.departureHubId(from)
					.arriveHubId(to)
					.routeInfo(routeInfo)
					.build());
			}
		}

		return shortestPath;
	}

	// 두 허브가 연결되어 있는지 확인
	private boolean isConnected(String fromName, String toName) {
		if (fromName == null || toName == null)
			return false;
		Set<String> fromConnections = HUB_CONNECTIONS.get(fromName);
		Set<String> toConnections = HUB_CONNECTIONS.get(toName);
		return (fromConnections != null && fromConnections.contains(toName)) ||
			(toConnections != null && toConnections.contains(fromName));
	}

	// 도착 허브 정보 노드
	static class ArrivalHub {
		UUID hubId;
		Integer hubToHubTime;
		double distance;

		ArrivalHub(UUID hubId, Integer hubToHubTime, double distance) {
			this.hubId = hubId;
			this.hubToHubTime = hubToHubTime;
			this.distance = distance;
		}
	}

	// 경로 결과 저장용 클래스
	static class PathResult {
		List<UUID> path;
		Integer totalTime;
		double totalDistance;

		PathResult(List<UUID> path, Integer totalTime, double totalDistance) {
			this.path = path;
			this.totalTime = totalTime;
			this.totalDistance = totalDistance;
		}
	}

	// 허브 간 네트워크를 나타내는 클래스
	class HubNetwork {
		List<Map<UUID, ArrivalHub>> adjList; // 허브 간 연결 정보 저장용 인접 리스트
		Map<UUID, Integer> hubIndex; // UUID와 허브 정보를 담은 리스트와 매칭하기 위한 인덱스 매핑용 맵

		HubNetwork(List<UUID> hubIds) {
			adjList = new LinkedList<>();
			hubIndex = new HashMap<>();
			int index = 0;
			for (UUID id : hubIds) {
				adjList.add(new HashMap<>());
				hubIndex.put(id, index++);
			}
		}

		// 간선 추가
		void addEdge(UUID from, UUID to, Integer hubToHubTime, Double distance) {
			Integer fromIndex = hubIndex.get(from);
			Integer toIndex = hubIndex.get(to);
			if (fromIndex == null || toIndex == null) {
				//todo: 커스텀 에러 처리?
				throw new IllegalArgumentException("UUID가 비었음");
			}
			adjList.get(fromIndex).put(to, new ArrivalHub(to, hubToHubTime, distance));
			adjList.get(toIndex).put(from, new ArrivalHub(from, hubToHubTime, distance));
		}

		// 다익스트라
		PathResult dijkstra(UUID start, UUID end, Set<UUID> visited) {
			Map<UUID, Double> distances = new HashMap<>(); // 총 거리
			Map<UUID, Integer> times = new HashMap<>(); // 총 시간
			Map<UUID, UUID> previous = new HashMap<>(); // 경로 추적용(역추적)
			Set<UUID> done = new HashSet<>(); //도달한 허브 체크용
			PriorityQueue<ArrivalHub> pq = new PriorityQueue<>(
				(a, b) -> {
					int cmp = Double.compare(a.distance, b.distance);
					return (cmp != 0) ? cmp : Integer.compare(a.hubToHubTime, b.hubToHubTime);
				}
			);

			// 허브 정보 테이블 초기화
			for (UUID hub : hubIndex.keySet()) {
				distances.put(hub, Double.MAX_VALUE);
				times.put(hub, Integer.MAX_VALUE);
			}

			//시작점 설정
			distances.put(start, 0.0);
			times.put(start, 0);
			pq.offer(new ArrivalHub(start, 0, 0.0));

			while (!pq.isEmpty()) {
				ArrivalHub current = pq.poll();
				UUID currentHub = current.hubId;

				//이미 탐색된 적이 있는 허브면 제외
				if (done.contains(currentHub)){
					continue;
				}

				done.add(currentHub);

				//기저조건
				if (currentHub.equals(end)){
					break;
				}

				int currentIndex = hubIndex.get(currentHub);
				for (ArrivalHub edge : adjList.get(currentIndex).values()) {
					if (visited.contains(edge.hubId))
						continue;

					double newDistance = distances.get(currentHub) + edge.distance;
					int newTime = times.get(currentHub) + edge.hubToHubTime;

					//경로 최신화
					if (newDistance < distances.get(edge.hubId)) {
						distances.put(edge.hubId, newDistance);
						times.put(edge.hubId, newTime);
						previous.put(edge.hubId, currentHub);
						pq.offer(new ArrivalHub(edge.hubId, newTime, newDistance));
					} else if (newDistance == distances.get(edge.hubId) && newTime < times.get(edge.hubId)) {
						times.put(edge.hubId, newTime);
						previous.put(edge.hubId, currentHub);
						pq.offer(new ArrivalHub(edge.hubId, newTime, newDistance));
					}
				}
			}

			//역추적
			List<UUID> path = new ArrayList<>();
			for (UUID at = end; at != null; at = previous.get(at)) {
				path.add(at);
			}
			Collections.reverse(path);

			if (path.isEmpty() || !path.get(0).equals(start)) {
				return new PathResult(new ArrayList<>(), -1, -1.0);
			}
			return new PathResult(path, times.get(end), distances.get(end));
		}

		// 최단 경로 계산(각 허브에 도달했을 때마다 다익스트라 실행)
		PathResult findPathWithOptimizeRelay(UUID start, UUID end) {
			PathResult bestPath = new PathResult(new ArrayList<>(), Integer.MAX_VALUE, Double.MAX_VALUE);
			Set<UUID> visited = new HashSet<>();
			visited.add(start);

			for (UUID relay : hubIndex.keySet()) {
				if (relay.equals(start) || relay.equals(end)){
					continue;
				}

				// 출발지 -> 경유지
				PathResult pathToRelay = dijkstra(start, relay, visited);
				if (pathToRelay.totalDistance == -1)
				{
					continue;
				}

				// 경유지까지의 방문 기록 추가
				Set<UUID> visitedToRelay = new HashSet<>(visited);
				visitedToRelay.addAll(pathToRelay.path);

				// 경유지 -> 도착지
				PathResult pathFromRelay = dijkstra(relay, end, visitedToRelay);
				if (pathFromRelay.totalDistance == -1){
					continue;
				}

				double totalDistance = pathToRelay.totalDistance + pathFromRelay.totalDistance;
				int totalTime = pathToRelay.totalTime + pathFromRelay.totalTime;

				//더 최적화된 경로가 되었으면 최신화
				if (totalDistance < bestPath.totalDistance ||
					(totalDistance == bestPath.totalDistance && totalTime < bestPath.totalTime)) {
					List<UUID> combinedPath = new ArrayList<>(pathToRelay.path);
					combinedPath.addAll(pathFromRelay.path.subList(1, pathFromRelay.path.size()));
					bestPath = new PathResult(combinedPath, totalTime, totalDistance);
				}
			}

			return bestPath;
		}
	}
}