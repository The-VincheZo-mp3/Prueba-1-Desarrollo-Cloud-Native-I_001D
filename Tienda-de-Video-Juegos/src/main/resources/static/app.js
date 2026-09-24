let juegosGlobales = [];
let carrito = [];

document.addEventListener('DOMContentLoaded', () => {
    cargarVideojuegos();
    setupEventListeners();
});

const formatearCLP = (monto) => {
    return new Intl.NumberFormat('es-CL', { style: 'currency', currency: 'CLP' }).format(monto);
};

function setupEventListeners() {
    document.getElementById('search-input')?.addEventListener('input', (e) => {
        const query = e.target.value.toLowerCase();
        const filtrados = juegosGlobales.filter(juego => 
            juego.titulo.toLowerCase().includes(query) || 
            (juego.categoria && juego.categoria.toLowerCase().includes(query)) ||
            (juego.creador && juego.creador.toLowerCase().includes(query))
        );
        renderizarTarjetas(filtrados);
    });

    const modal = document.getElementById('modal-juego');
    document.getElementById('btn-open-modal')?.addEventListener('click', () => modal.classList.remove('hidden'));
    document.getElementById('btn-close-modal')?.addEventListener('click', () => modal.classList.add('hidden'));

    const drawer = document.getElementById('cart-drawer');
    document.getElementById('btn-open-cart')?.addEventListener('click', () => drawer.classList.remove('translate-x-full'));
    document.getElementById('btn-close-cart')?.addEventListener('click', () => drawer.classList.add('translate-x-full'));

    document.getElementById('form-juego')?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const nuevoJuego = {
            titulo: document.getElementById('titulo').value,
            categoria: document.getElementById('categoria').value,
            creador: document.getElementById('creador').value,
            precio: parseFloat(document.getElementById('precio').value),
            descuento: parseFloat(document.getElementById('descuento').value) || 0,
            imagenUrl: document.getElementById('imagenUrl').value,
            descripcion: document.getElementById('descripcion').value
        };

        try {
            const res = await fetch('/api/videojuegos', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(nuevoJuego)
            });
            if (res.ok) {
                modal.classList.add('hidden');
                document.getElementById('form-juego').reset();
                cargarVideojuegos();
            }
        } catch (err) {
            console.error('Error al guardar el juego:', err);
        }
    });

    document.getElementById('btn-checkout')?.addEventListener('click', () => {
        if (carrito.length === 0) return alert('El carrito está vacío.');
        alert('¡Gracias por tu compra en PixelVault!');
        carrito = [];
        actualizarCarritoUI();
        drawer.classList.add('translate-x-full');
    });
}

async function cargarVideojuegos() {
    try {
        const res = await fetch('/api/videojuegos');
        if (!res.ok) throw new Error('Error al cargar juegos');
        juegosGlobales = await res.json();
        renderizarTarjetas(juegosGlobales);
    } catch (err) {
        console.error('Error:', err);
    }
}

function renderizarTarjetas(juegos) {
    const contenedor = document.getElementById('contenedor-juegos');
    if (!contenedor) return;

    if (juegos.length === 0) {
        contenedor.innerHTML = `<p class="text-slate-400 text-center col-span-full py-12">No hay videojuegos disponibles.</p>`;
        return;
    }

    contenedor.innerHTML = juegos.map(juego => {
        const tieneDescuento = juego.descuento && juego.descuento > 0;
        const precioFinal = tieneDescuento 
            ? juego.precio * (1 - juego.descuento / 100) 
            : juego.precio;

        return `
            <div class="bg-slate-900 border border-slate-800 hover:border-purple-500/50 rounded-2xl p-4 flex flex-col justify-between transition-all duration-300 group shadow-lg">
                <div class="relative overflow-hidden rounded-xl mb-3">
                    <img src="${juego.imagenUrl || 'https://via.placeholder.com/300x180'}" alt="${juego.titulo}" class="w-full h-44 object-cover group-hover:scale-105 transition duration-300">
                    <span class="absolute top-2 left-2 bg-slate-950/80 backdrop-blur-md text-purple-300 text-xs px-2.5 py-1 rounded-full border border-purple-500/30">${juego.categoria || 'Juego'}</span>
                    ${tieneDescuento ? `<span class="absolute top-2 right-2 bg-pink-600 text-white font-bold text-xs px-2 py-1 rounded-lg">-${juego.descuento}%</span>` : ''}
                </div>
                <div class="mb-4">
                    <h3 class="text-lg font-bold text-white mb-0.5 leading-snug">${juego.titulo}</h3>
                    <p class="text-xs text-purple-400 font-medium mb-2"><i class="fa-solid fa-code text-[10px] mr-1"></i>${juego.creador || 'Desarrollador Indio'}</p>
                    <p class="text-slate-400 text-xs line-clamp-2">${juego.descripcion || ''}</p>
                </div>
                <div class="flex items-center justify-between pt-3 border-t border-slate-800/80 mt-auto">
                    <div>
                        ${tieneDescuento ? `<span class="text-xs text-slate-500 line-through block">${formatearCLP(juego.precio)}</span>` : ''}
                        <span class="text-xl font-black text-emerald-400">${formatearCLP(precioFinal)}</span>
                    </div>
                    <button onclick="agregarAlCarrito(${juego.id})" class="bg-purple-600 hover:bg-purple-500 text-white p-2.5 rounded-xl transition flex items-center justify-center shadow-lg shadow-purple-600/20">
                        <i class="fa-solid fa-cart-plus"></i>
                    </button>
                </div>
            </div>
        `;
    }).join('');
}

function agregarAlCarrito(id) {
    const juego = juegosGlobales.find(j => j.id === id);
    if (juego) {
        carrito.push(juego);
        actualizarCarritoUI();
    }
}

function eliminarDelCarrito(index) {
    carrito.splice(index, 1);
    actualizarCarritoUI();
}

function actualizarCarritoUI() {
    const count = document.getElementById('cart-count');
    const container = document.getElementById('cart-items');
    const totalEl = document.getElementById('cart-total');

    if (count) count.textContent = carrito.length;

    let total = 0;

    if (container) {
        if (carrito.length === 0) {
            container.innerHTML = `<p class="text-slate-500 text-center py-8">Tu carrito está vacío.</p>`;
        } else {
            container.innerHTML = carrito.map((item, index) => {
                const tieneDescuento = item.descuento && item.descuento > 0;
                const precioFinal = tieneDescuento ? item.precio * (1 - item.descuento / 100) : item.precio;
                total += precioFinal;

                return `
                    <div class="flex items-center justify-between bg-slate-800/50 p-3 rounded-xl border border-slate-700/50">
                        <div class="flex items-center space-x-3">
                            <img src="${item.imagenUrl}" class="w-12 h-12 rounded-lg object-cover">
                            <div>
                                <h4 class="text-sm font-bold text-white">${item.titulo}</h4>
                                <p class="text-[10px] text-purple-300">Por: ${item.creador || 'N/A'}</p>
                                <div class="flex items-center gap-1.5">
                                    <span class="text-xs text-emerald-400 font-semibold">${formatearCLP(precioFinal)}</span>
                                    ${tieneDescuento ? `<span class="text-[10px] bg-pink-500/20 text-pink-300 px-1 rounded">-${item.descuento}%</span>` : ''}
                                </div>
                            </div>
                        </div>
                        <button onclick="eliminarDelCarrito(${index})" class="text-slate-500 hover:text-red-400 transition p-1">
                            <i class="fa-solid fa-trash"></i>
                        </button>
                    </div>
                `;
            }).join('');
        }
    }

    if (totalEl) totalEl.textContent = formatearCLP(total);
}