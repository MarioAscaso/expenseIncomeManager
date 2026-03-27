let calendar;

function loadTransactionsFromBackend() {
    fetch(`${API_BASE_URL}/movements?userId=${currentUser.id}`)
        .then(res => res.json())
        .then(data => {
            if(calendar) {
                calendar.removeAllEvents();
                calendar.addEventSource(data);
            }
        })
        .catch(err => console.error('Error cargando movimientos:', err));
}

document.addEventListener('DOMContentLoaded', function() {
    
    const calendarElement = document.getElementById('calendar');
    if(!calendarElement) return;

    calendar = new FullCalendar.Calendar(calendarElement, {
        initialView: 'dayGridMonth',
        locale: 'es',
        height: '100%', 
        
        // LA PROPIEDAD MÁGICA: Fuerza a FullCalendar a que las filas 
        // ocupen el 100% del alto y se repartan de forma idéntica.
        expandRows: true,               
        
        headerToolbar: false,           
        showNonCurrentDates: false,     
        fixedWeekCount: false,          
        firstDay: 1,                    
        events: [],
        
        eventDidMount: function(info) {
            const isIncome = info.event.extendedProps.type === 'INCOME';
            info.el.style.backgroundColor = isIncome ? '#2a9d8f' : '#e63946'; 
        },
        
        datesSet: function(info) {
            const titleEl = document.getElementById('customCalendarTitle');
            if(titleEl) titleEl.innerText = info.view.title;
        },

        dateClick: (info) => {
            if (typeof prepareModalForCreation === 'function') {
                prepareModalForCreation(info.dateStr);
                new bootstrap.Modal(document.getElementById('transactionModal')).show();
            }
        },
        
        eventClick: (info) => {
            if (['admin', 'superadmin', 'basic'].includes(currentUser.role)) {
                if (typeof prepareModalForEdition === 'function') {
                    prepareModalForEdition(info.event);
                    new bootstrap.Modal(document.getElementById('transactionModal')).show();
                }
            }
        }
    });
    
    calendar.render();

    const btnPrev = document.getElementById('btnPrevMonth');
    const btnNext = document.getElementById('btnNextMonth');
    const btnToday = document.getElementById('btnToday');

    if(btnPrev) btnPrev.addEventListener('click', () => calendar.prev());
    if(btnNext) btnNext.addEventListener('click', () => calendar.next());
    if(btnToday) btnToday.addEventListener('click', () => calendar.today());

    refreshDashboard(); 
});