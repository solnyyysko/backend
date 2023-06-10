import React, {useId, useState, useEffect} from 'react';
import {useNavigate, useParams} from "react-router-dom";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faChevronLeft, faSave} from "@fortawesome/free-solid-svg-icons";
import {alertActions} from "../utils/Rdx";
import {connect, useDispatch} from "react-redux";
import BackendService from "../services/BackendService";

const ArtistComponent = props => {

    const [Name, setName] = useState('');
    const [Century, setCentury] = useState('');
    const [Country, setCountry] = useState(0);
    const [Countries, setCountries] = useState([]);
    const [hidden, setHidden] = useState(false);
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const nameId = useId();
    const centId = useId();
    const countryId = useId();
    const params = useParams();

    const countryList = () => {
        return Countries.map((country) => {
            return <option key={country.id} value={country.id}>{country.name}
            </option>;
        });
    }

    const loadArtist = () => {
        BackendService.retrieveArtist(params.id)
            .then(
                resp => {
                    setName(resp.data.name);
                    setCentury(resp.data.century)
                    setCountry(resp.data.country.id)
                    //console.log("COUNTRY = " + resp.data.country.id)
                    setHidden(false)
                })
            .catch(() => {
                setHidden(true)
            })
    }
    const loadCountries = () => {
        BackendService.retrieveAllCountries()
            .then(
                resp => {
                    setCountries(resp.data)
                    //console.log("data = " + resp.data)
                }
            )
    }

    useEffect(() => {
        loadCountries();
        if (parseInt(params.id) !== -1) {
            loadArtist();
        }
    }, [])

    const onSubmit = e => {
        e.preventDefault();
        const form = e.target;
        const formData = new FormData(form);
        const formJson = Object.fromEntries(formData.entries());
        console.log("formJson = " + JSON.stringify(formJson))
        let err = null;
        if (!formJson.name) {
            err = "Имя художника должно быть указано";
        }
        if (err) {
            dispatch(alertActions.error(err))
        }
        let artist = { id: params.id,
            name: formJson.name,
            century: formJson.century,
            country: { id: formJson.country }
        };
        if (parseInt(artist.id) === -1) {
            BackendService.createArtist(artist)
                .then(()=> navigate("/artists"))
                .catch(()=> {})
        }
        else {
            BackendService.updateArtist(artist)
                .then(()=> navigate("/artists"))
                .catch(()=>{})
        }
    }

    if (hidden)
        return null;
    return (
        <div className="container">
            <div className="row">
                <div className="col-md-6">
                    <h3>Художник</h3>
                </div>
                <div className="col-md-6 clearfix">
                    <button className="btn btn-outline-secondary float-end"
                            onClick={ () => { navigate(-1) } }>
                        <FontAwesomeIcon icon={faChevronLeft}/>{' '}Назад</button>
                </div>
            </div>
            <div className="row">
                <div className="col-md-12">
                    <form method="post" onSubmit={onSubmit}>
                        <label className="form-label" htmlFor={nameId}>Имя:</label>
                        <input id={nameId} name="name"
                               className="form-control"
                               defaultValue={Name}
                               autoComplete="off"/>
                        <label className="form-label" htmlFor={centId}>Век:</label>
                        <input id={centId} name="century"
                               className="form-control"
                               defaultValue={Century}
                               autoComplete="off"/>
                        <label className="form-label" htmlFor={countryId}>Страна:</label>
                        <select id={countryId}
                                name="country"
                                className="form-control"
                                onChange={(e) => {
                                    console.log(e.target.value)
                                    setCountry(e.target.value);
                                }}
                                value={Country}>
                            {countryList()}
                        </select>
                        <button
                            className="btn btn-outline-secondary mt-4"
                            type="submit">
                            <FontAwesomeIcon icon={faSave}/>{' '}Сохранить</button>
                    </form>
                </div>
            </div>
        </div>
    )
}

export default connect()(ArtistComponent);