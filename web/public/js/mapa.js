import { map, tileLayer, geoJSON } from "leaflet";

export function plotaMapa(ocorrencias){
	const mapa = map('map').setView([-3.099261, -59.977954], 100);

	tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
	    maxZoom: 19,
	    attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
	}).addTo(mapa);

	const markers = [];

	ocorrencias.forEach(function(ocor) {
	    if (!ocor.origem_gps || !ocor.id) return;
	    const marker = geoJSON(ocor.origem_gps).addTo(mapa);
	    marker.bindPopup(
		`
		<b>${ocor.classificacao}</b><br>
		Data: ${ocor.data_captura}<br>
		Risco: ${ocor.risco}<br>
		${(ocor.status_saude ? `Saúde: ${ocor.status_saude}<br>` : '')}
		<a href="/ocorrencias/${ocor.id}" class="btn btn-sm btn-outline-primary mt-1">Detalhes</a>
		`
	    );
	    markers.push(marker);
	});
}
