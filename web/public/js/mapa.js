const map = L.map('map').setView([-14.2350, -51.9253], 4);

L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
}).addTo(map);

const markers = [];

ocorrencias.forEach(function(ocor) {
    if (!ocor.origem_gps) return;
    const coords = JSON.parse(ocor.origem_gps);
    if (!coords) return;

    const marker = L.marker(parseFloat(coords[1]), parseFloat(coords[0])).addTo(map);
    marker.bindPopup(
        `
        <b>${ocor.classificacao}</b><br>
        Data: ${ocor.data_captura}<br>
        Risco: ${ocrr.risco}<br>
        ${(ocor.status_saude ? `Saúde: ${ocor.status_saude}<br>` : '')}
        <a href="/ocorrencias/${ocor.id}" class="btn btn-sm btn-outline-primary mt-1">Detalhes</a>
        `
    );
    markers.push(marker);
});

if (markers.length > 0) {
    const group = L.featureGroup(markers);
    map.fitBounds(group.getBounds().pad(0.1));
}