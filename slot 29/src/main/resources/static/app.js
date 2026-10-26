// Author: Phan Tuan Thanh
'use strict';
const $ = selector => document.querySelector(selector);
let library = { students: [], books: [] };
function node(tag, text, className) {
  const element = document.createElement(tag);
  if (text !== undefined) element.textContent = text;
  if (className) element.className = className;
  return element;
}
function notice(message, error = false) {
  $('#notice').textContent = message;
  $('#notice').className = error ? 'error' : '';
  $('#notice').hidden = false;
}
async function request(path, method = 'GET', body) {
  const response = await fetch('/api' + path, {
    method, headers: { 'Content-Type': 'application/json' },
    body: body === undefined ? undefined : JSON.stringify(body)
  });
  if (!response.ok) {
    const error = await response.json().catch(() => ({}));
    throw new Error(error.message || (response.status === 400 ? 'Vui lòng kiểm tra dữ liệu nhập (email, GPA và các trường bắt buộc).' : 'Không thể xử lý yêu cầu. Vui lòng thử lại.'));
  }
  return response.status === 204 ? null : response.json();
}
function render() {
  $('#student-count').textContent = library.students.length;
  $('#book-count').textContent = library.books.length;
  $('#loan-count').textContent = library.students.reduce((sum, student) => sum + student.books.length, 0);
  const rows = $('#student-rows'); rows.replaceChildren();
  for (const student of library.students) {
    const row = node('tr');
    row.append(node('td', student.firstName + ' ' + student.lastName), node('td', student.email), node('td', student.gpa.toFixed(1)));
    const loans = node('td');
    if (!student.books.length) loans.append(node('span', 'Chưa mượn sách', 'empty'));
    for (const book of student.books) {
      const loan = node('div', undefined, 'loan');
      const button = node('button', 'Trả'); button.type = 'button';
      button.setAttribute('aria-label', 'Trả ' + book.title + ' của ' + student.firstName + ' ' + student.lastName);
      button.addEventListener('click', () => perform(button, async () => {
        await request(`/students/${student.id}/books/${book.id}`, 'DELETE');
      }, 'Đã trả sách thành công.'));
      loan.append(node('span', book.title), button); loans.append(loan);
    }
    row.append(loans); rows.append(row);
  }
  if (!library.students.length) { const row = node('tr'); const cell = node('td', 'Chưa có sinh viên. Thêm sinh viên đầu tiên bên dưới.', 'empty'); cell.colSpan = 4; row.append(cell); rows.append(row); }
  const bookList = $('#book-list'); bookList.replaceChildren();
  for (const book of library.books) {
    const card = node('article', undefined, 'book-card');
    card.append(node('small', 'ISBN / ' + book.isbn), node('h3', book.title), node('p', book.author)); bookList.append(card);
  }
  if (!library.books.length) bookList.append(node('p', 'Kho sách đang trống. Thêm một đầu sách bên dưới.', 'empty'));
  fillSelect($('#student-select'), library.students, student => student.firstName + ' ' + student.lastName + ' · ' + student.email, 'Chọn sinh viên');
  fillSelect($('#book-select'), library.books, book => book.title + ' · ' + book.isbn, 'Chọn sách');
}
function fillSelect(select, items, label, placeholder) {
  const selected = select.value;
  const first = node('option', placeholder); first.value = '';
  select.replaceChildren(first);
  for (const item of items) { const option = node('option', label(item)); option.value = item.id; select.append(option); }
  select.value = [...select.options].some(option => option.value === selected) ? selected : '';
}
async function load() { library = await request('/library'); render(); }
async function perform(button, action, message) {
  button.disabled = true;
  try { await action(); notice(message); await load(); }
  catch (error) { notice(error.message, true); }
  finally { button.disabled = false; }
}
$('#student-form').addEventListener('submit', event => {
  event.preventDefault(); const form = event.currentTarget;
  const data = Object.fromEntries(new FormData(form)); data.gpa = Number(data.gpa);
  perform(form.querySelector('button'), async () => { await request('/students', 'POST', data); form.reset(); }, 'Đã thêm sinh viên.');
});
$('#book-form').addEventListener('submit', event => {
  event.preventDefault(); const form = event.currentTarget;
  perform(form.querySelector('button'), async () => { await request('/books', 'POST', Object.fromEntries(new FormData(form))); form.reset(); }, 'Đã thêm sách.');
});
$('#borrow-form').addEventListener('submit', event => {
  event.preventDefault(); const form = event.currentTarget; const data = Object.fromEntries(new FormData(form));
  perform(form.querySelector('button'), () => request(`/students/${data.studentId}/books/${data.bookId}`, 'POST'), 'Đã mượn sách thành công.');
});
$('#refresh').addEventListener('click', event => perform(event.currentTarget, async () => {}, 'Đã cập nhật dữ liệu.'));
load().catch(error => notice('Không tải được dữ liệu: ' + error.message, true));
