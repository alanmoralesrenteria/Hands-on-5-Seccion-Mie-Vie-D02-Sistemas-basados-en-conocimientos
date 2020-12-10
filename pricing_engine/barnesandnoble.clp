(import pricing_engine.*)
(deftemplate Order       (declare (from-class Order)))
(deftemplate OrderItem   (declare (from-class OrderItem)))
(deftemplate CatalogItem (declare (from-class CatalogItem)))
(deftemplate Customer    (declare (from-class Customer)))
(deftemplate CreditCard  (declare (from-class CreditCard)))


(defrule 10%-coupon "Obten un cupon del 10% para tu proxima compras, en compras mayores a $500 . (Solo articulos participantes)" ?o <- (Order {total > 500})
    => (add (new Offer "Obten un cupon del 10% para tu proxima compras en compras mayores a $500 . (Solo articulos participantes)")))

(defrule 24-meses-master-card "Obten 24 meses sin intereses al pargar con tarjetas Master-Card" ?o <- (Order (tarjeta "Master-Card"))
    => (add (new Offer "Obten 24 meses sin intereses al pagar con tarjetas Master-Card" ?o.tarjeta)))

(defrule 12-meses-coppel "Obten 12 meses sin intereses al pargar con tarjetas Coppel" ?o <- (Order (tarjeta "Coppel"))
    => (add (new Offer "Obten 12 meses sin intereses al pagar con tarjetas Coppel" ?o.tarjeta)))

(defrule barns-vales "En la compra de la coleccion del señor de los anillos, obten 100 pesos en vales por cada 1000 pesos de compra" (OrderItem {partNumber == 420 } (price ?price))
    => (add (new Offer "En la compra de la coleccion del señor de los anillos, obten 100 pesos en vales por cada 1000 pesos de compra" ?price "true" )))

(defrule books-promo "En la compra de Isolation + Hello universe + The book wanderer , obten un vale de $100 en tu proxima compra." (OrderItem {partNumber == 422}) (OrderItem {partNumber == 423}) (OrderItem {partNumber == 424})
        => (add (new Offer "En la compra de Isolation + Hello universe + The book wanderer , obten un vale de $100 en tu proxima compra." 100)))

(defrule 3%-book-item-discount "Obten un 3% de descuento en la compra de mas de 5 articulos identicos (mayoreo)." ?o<-(OrderItem {quantity >= 3} (price ?price) (description ?itemdescription))
    => (add (new Offer "Obten un 3% de descuento en la compra de mas de 3 articulos identicos(mayoreo)." ?itemdescription (* (* ?price 0.03) ?o.quantity) )))